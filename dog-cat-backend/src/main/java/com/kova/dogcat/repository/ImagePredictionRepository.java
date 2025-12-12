package com.kova.dogcat.repository;

import com.kova.dogcat.entity.ImagePrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImagePredictionRepository extends JpaRepository<ImagePrediction, UUID> {
}