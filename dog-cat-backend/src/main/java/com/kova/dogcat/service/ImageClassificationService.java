package com.kova.dogcat.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ImageClassificationService {

    public PredictionResult classify(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);

        if (lower.contains("dog")) {
            return new PredictionResult("DOG", 0.95);
        }
        if (lower.contains("cat")) {
            return new PredictionResult("CAT", 0.95);
        }
        return new PredictionResult("UNKNOWN", 0.50);
    }

    public record PredictionResult(String label, double confidence) {}
}