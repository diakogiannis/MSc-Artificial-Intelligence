package com.diakogiannis.nostradamus.astroapi_service.western.web;

import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyRequest;
import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyResponse;
import com.diakogiannis.nostradamus.astroapi_service.western.services.AstrologyPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/astrology")
public class AstrologyController {

    private final AstrologyPredictionService predictionService;

    public AstrologyController(AstrologyPredictionService predictionService) {
        this.predictionService = predictionService;
    }


    @PostMapping(path="/predict/western")
    public Flux<AstrologyResponse> predict(@RequestBody AstrologyRequest request) {
        return predictionService.callOpenRouter(request);
    }
}