package com.kova.dogcat.service;

import com.google.cloud.vision.v1.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SafeSearchServiceTest {
    @ParameterizedTest
    @CsvSource({
            "UNLIKELY, POSSIBLE, 200",
            "LIKELY, UNLIKELY, 422",
            "UNKNOWN, VERY_LIKELY, 422",
            "UNLIKELY, UNKNOWN, 503"
    })
    void checksBothRatings(Likelihood adult, Likelihood violence, int expectedStatus) {
        ImageAnnotatorClient client = mock();
        ObjectProvider<ImageAnnotatorClient> clients = mock();
        when(clients.getObject()).thenReturn(client);
        var annotation = SafeSearchAnnotation.newBuilder().setAdult(adult).setViolence(violence);
        var response = AnnotateImageResponse.newBuilder().setSafeSearchAnnotation(annotation);
        when(client.batchAnnotateImages(anyList())).thenReturn(
                BatchAnnotateImagesResponse.newBuilder().addResponses(response).build());

        var service = new SafeSearchService(clients);
        byte[] content = {1, 2, 3};
        if (expectedStatus == 200) {
            assertDoesNotThrow(() -> service.check(content));
        } else {
            var error = assertThrows(ResponseStatusException.class, () -> service.check(content));
            assertEquals(expectedStatus, error.getStatusCode().value());
        }
    }
}
