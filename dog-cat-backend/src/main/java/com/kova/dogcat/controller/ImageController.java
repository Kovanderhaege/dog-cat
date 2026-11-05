package com.kova.dogcat.controller;

import com.kova.dogcat.dto.ImagePredictionDto;
import com.kova.dogcat.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService service;
    private final ImageRequestLimit uploads = new ImageRequestLimit();
    private final ImageRequestLimit predictions = new ImageRequestLimit();

    public ImageController(ImageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UUID> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        long delay = uploads.retryAfter(request.getRemoteAddr());
        if (delay > 0) return ResponseEntity.status(429).header("Retry-After", Long.toString(delay)).build();
        UUID id = service.uploadImage(file);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagePredictionDto> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getImage(id));
    }

    @GetMapping
    public ResponseEntity<List<ImagePredictionDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getFile(@PathVariable UUID id) throws IOException {
        ImagePredictionDto dto = service.getImage(id);
        Path file = Path.of(dto.filePath());

        byte[] data = Files.readAllBytes(file);

        return ResponseEntity.ok()
                .header("Content-Type", Files.probeContentType(file))
                .body(data);
    }

    @PostMapping("/{id}/predict")
    public ResponseEntity<Void> predict(@PathVariable UUID id, HttpServletRequest request) {
        long delay = predictions.retryAfter(request.getRemoteAddr());
        if (delay > 0) return ResponseEntity.status(429).header("Retry-After", Long.toString(delay)).build();
        service.predict(id);
        return ResponseEntity.ok().build();
    }
}