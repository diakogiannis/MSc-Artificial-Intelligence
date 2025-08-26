package com.diakogiannis.nostradamus.astroapi_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.diakogiannis.nostradamus.astroapi_service.enums.ApiClient;

class ConcreteAstroResp extends AbstractAstrologyResponse {}

public class AbstractAstrologyResponseTest {

    @Test
    void gettersSettersAndToString() {
        ConcreteAstroResp r = new ConcreteAstroResp();

        r.setApiClient(ApiClient.GEMINI);
        r.setClientId("client-x");
        r.setError("none");

        assertEquals(ApiClient.GEMINI, r.getApiClient());
        assertEquals("client-x", r.getClientId());
        assertEquals("none", r.getError());

        // exercise Lombok-generated toString for coverage
        String s = r.toString();
        assertNotNull(s);
        assertTrue(s.contains("apiClient"));
        assertTrue(s.contains("clientId"));
    }
}
