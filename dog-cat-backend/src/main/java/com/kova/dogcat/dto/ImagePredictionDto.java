package com.kova.dogcat.dto;

import java.time.Instant;
import java.util.UUID;

public record ImagePredictionDto(
        UUID id,
        String filePath,
        String status,
        String predictedLabel,
        Double confidence,
        Instant createdAt
) {}