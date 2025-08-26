package com.diakogiannis.nostradamus.astroapi_service.dto;

import com.diakogiannis.nostradamus.astroapi_service.enums.ApiClient;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public abstract class AbstractAstrologyResponse {

    private Enum<ApiClient> apiClient;
    private String clientId;
    private String error;

}
