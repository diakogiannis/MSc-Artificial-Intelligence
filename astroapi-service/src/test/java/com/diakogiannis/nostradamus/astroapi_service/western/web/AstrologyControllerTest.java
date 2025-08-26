package com.diakogiannis.nostradamus.astroapi_service.western.web;

import com.diakogiannis.nostradamus.astroapi_service.enums.ApiClient;
import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyRequest;
import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyResponse;
import com.diakogiannis.nostradamus.astroapi_service.western.services.AstrologyPredictionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AstrologyControllerTest {

    @Test
    void predict_returnsFluxFromService() {
        AstrologyPredictionService mockService = Mockito.mock(AstrologyPredictionService.class);
        AstrologyController controller = new AstrologyController(mockService);

        AstrologyResponse resp = new AstrologyResponse();
        resp.setApiClient(ApiClient.GEMINI);
        resp.setClientId("client-1");

        when(mockService.callOpenRouter(any(AstrologyRequest.class)))
                .thenReturn(Flux.just(resp));

        Flux<AstrologyResponse> result = controller.predict(
                new AstrologyRequest("2000-01-01", "12:00", "Athens", 2025, "M")
        );

        StepVerifier.create(result)
                .expectNextMatches(r -> "client-1".equals(r.getClientId()) && r.getApiClient() == ApiClient.GEMINI)
                .verifyComplete();
    }
}
