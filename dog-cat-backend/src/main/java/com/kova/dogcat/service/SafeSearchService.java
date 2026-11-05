package com.kova.dogcat.service;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.rpc.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Configuration
class VisionConfiguration {
    @Bean(destroyMethod = "close")
    @Lazy
    ImageAnnotatorClient visionClient() throws IOException {
        return ImageAnnotatorClient.create();
    }
}

@Service
class SafeSearchService {
    private static final Logger log = LoggerFactory.getLogger(SafeSearchService.class);
    private final ObjectProvider<ImageAnnotatorClient> clients;

    SafeSearchService(ObjectProvider<ImageAnnotatorClient> clients) {
        this.clients = clients;
    }

    void check(byte[] content) {
        var request = AnnotateImageRequest.newBuilder()
                .setImage(Image.newBuilder().setContent(ByteString.copyFrom(content)))
                .addFeatures(Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION))
                .build();
        BatchAnnotateImagesResponse batch;
        try {
            batch = clients.getObject().batchAnnotateImages(List.of(request));
        } catch (ApiException | BeansException e) {
            log.warn("Vision SafeSearch request failed", e);
            throw unavailable();
        }
        if (batch.getResponsesCount() != 1) {
            throw unavailable();
        }
        var result = batch.getResponses(0);
        if (result.hasError()) {
            if (result.getError().getCode() == Code.INVALID_ARGUMENT_VALUE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image invalide.");
            }
            log.warn("Vision returned error code {}", result.getError().getCode());
            throw unavailable();
        }
        var annotation = result.getSafeSearchAnnotation();
        var ratings = List.of(annotation.getAdult(), annotation.getViolence());
        if (ratings.contains(Likelihood.LIKELY) || ratings.contains(Likelihood.VERY_LIKELY)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Image refusée pour contenu explicite ou violent.");
        }
        if (ratings.contains(Likelihood.UNKNOWN) || ratings.contains(Likelihood.UNRECOGNIZED)) {
            throw unavailable();
        }
    }

    private static ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Vérification de l’image indisponible. Réessaie plus tard.");
    }
}
