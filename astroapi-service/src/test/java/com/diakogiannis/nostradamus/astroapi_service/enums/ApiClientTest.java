package com.diakogiannis.nostradamus.astroapi_service.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ApiClientTest {

    @Test
    void enumBasics() {
        assertTrue(ApiClient.values().length >= 1);
        assertEquals(ApiClient.GEMINI, ApiClient.valueOf("GEMINI"));
        // toString should be the name by default for enums
        assertEquals("GEMINI", ApiClient.GEMINI.toString());
    }
}
