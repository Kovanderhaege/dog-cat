package com.hugo.dogcat.service;

import com.hugo.dogcat.dto.ImagePredictionDto;
import com.hugo.dogcat.entity.ImagePrediction;
import com.hugo.dogcat.repository.ImagePredictionRepository;
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

    public ImageService(ImagePredictionRepository repository) {
        this.repository = repository;
    }

    public UUID uploadImage(MultipartFile file) throws IOException {

        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destination = uploadDir.resolve(filename);

        Files.write(destination, file.getBytes());

        ImagePrediction img = new ImagePrediction();
        img.setFilePath(destination.toString());

        ImagePrediction saved = repository.save(img);

        return saved.getId();
    }

    public ImagePredictionDto getImage(UUID id) {
        return repository.findById(id)
                .map(img -> new ImagePredictionDto(
                        img.getId(),
                        img.getFilePath(),
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
                        img.getPredictedLabel(),
                        img.getConfidence(),
                        img.getCreatedAt()
                ))
                .toList();
    }
}