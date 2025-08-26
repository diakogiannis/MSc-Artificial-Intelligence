package com.diakogiannis.nostradamus.astroapi_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = AstroapiServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "openrouter.api.key=dummy",
                "openrouter.api.url=http://localhost",
                "openrouter.model=test-model"
        }
)
class AstroapiServiceApplicationSmokeTest {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test will fail.
    }
}
