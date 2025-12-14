package com.kova.dogcat.dto;

public record ClassificationResult(
        String label,
        double confidence
) {}