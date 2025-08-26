package com.diakogiannis.nostradamus.astroapi_service.western.services;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyRequest;

public class AstrologyPredictionServicePrivateMethodsTest {

    private AstrologyPredictionService newService() {
        // build with dummy config, no network calls will be made
        return new AstrologyPredictionService(WebClient.builder(),
                "dummy-key", "http://localhost", "gpt-test");
    }

    @Test
    void sanitizeSvgField_escapesCorrectly() throws Exception {
        AstrologyPredictionService svc = newService();
        String json = "{\"chart_svg\": \"<svg>\\n\\\"text\\\"</svg>\"}";

        Method m = AstrologyPredictionService.class
                .getDeclaredMethod("sanitizeSvgField", String.class);
        m.setAccessible(true);

        String out = (String) m.invoke(svc, json);

        // newline should be escaped
        assertTrue(out.contains("\\n"), "newlines should be escaped to \\n");
        // quotes inside SVG should be escaped
        assertTrue(out.contains("\\\"text\\\""), "quotes should be escaped as \\\"...\\\"");
        // sanity: no raw newline should remain
        assertFalse(out.contains("\n"), "raw newlines should not remain");
    }

    @Test
    void buildPrompt_returnsNonEmptyPrompt() throws Exception {
        AstrologyPredictionService svc = newService();

        Method m = AstrologyPredictionService.class
                .getDeclaredMethod("buildPrompt", AstrologyRequest.class);
        m.setAccessible(true);

        String prompt = (String) m.invoke(svc,
                new AstrologyRequest("2000-01-01", "12:00", "Athens", 2025, "M"));

        assertNotNull(prompt);
        assertTrue(prompt.contains("Τεχνικό Παράρτημα"), "Greek technical header missing");
        assertTrue(prompt.length() > 50, "Prompt seems too short");
    }
}
