package com.kova.dogcat.service;

import com.kova.dogcat.dto.ImagePredictionDto;
import com.kova.dogcat.entity.ImagePrediction;
import com.kova.dogcat.entity.PredictionStatus;
import com.kova.dogcat.repository.ImagePredictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImagePredictionRepository repository;

    private final ImageClassificationService classifier;

    public ImageService(ImagePredictionRepository repository,
                        ImageClassificationService classifier) {
        this.repository = repository;
        this.classifier = classifier;
    }

    public UUID uploadImage(MultipartFile file) throws IOException {

        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destination = uploadDir.resolve(filename);

        Files.write(destination, file.getBytes());

        ImagePrediction img = new ImagePrediction();
        img.setFilePath(destination.toString());
        img.setStatus(PredictionStatus.PENDING);

        ImagePrediction saved = repository.save(img);

        return saved.getId();
    }

    public ImagePredictionDto getImage(UUID id) {
        return repository.findById(id)
                .map(img -> new ImagePredictionDto(
                        img.getId(),
                        img.getFilePath(),
                        img.getStatus().name(),
                        img.getPredictedLabel(),
                        img.getConfidence(),
                        img.getCreatedAt()
                ))
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public List<ImagePredictionDto> getAll() {
        return repository.findAll()
                .stream()
                .map(img -> new ImagePredictionDto(
                        img.getId(),
                        img.getFilePath(),
                        img.getStatus().name(),
                        img.getPredictedLabel(),
                        img.getConfidence(),
                        img.getCreatedAt()
                ))
                .toList();
    }

    public void predict(UUID id) {
        ImagePrediction img = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        img.setStatus(PredictionStatus.PENDING);

        try {
            var result = classifier.classify(Path.of(img.getFilePath()));

            img.setPredictedLabel(result.label());
            img.setConfidence(result.confidence());
            img.setStatus(PredictionStatus.DONE);

        } catch (Exception e) {
            img.setStatus(PredictionStatus.ERROR);
        }

        repository.save(img);
    }
}