package com.diakogiannis.nostradamus.astroapi_service.western.dto;

import lombok.ToString;

public record AstrologyRequest(
        String birthDate,
        String birthTime,
        String address,
        Integer yearOfForecast,
        String sex
) {}

