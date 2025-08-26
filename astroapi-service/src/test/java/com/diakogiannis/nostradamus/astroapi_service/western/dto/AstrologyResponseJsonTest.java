package com.diakogiannis.nostradamus.astroapi_service.western.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AstrologyResponseJsonTest {

    @Test
    void deserializeAndRoundTrip() throws Exception {
        String json = """
        {
          "clientId":"c1",
          "report":{
            "career_guidance":{
              "ideal_environment":"remote",
              "tips":["t1","t2"],
              "suggested_roles":["r1","r2"],
              "final_summary":"done",
              "main_axes":"axisA | axisB",
              "paths":["path1","path2"]
            },
            "planets":{
              "sun":{"sign":"Aries","house":"1","degree":"10","interpretation":"x","technical_interpretation":"tx"},
              "moon":{"sign":"Taurus","house":"2","degree":"20","interpretation":"y","technical_interpretation":"ty"}
            },
            "ascendant":{
              "sign":"Gemini",
              "degree":"15",
              "interpretation":"z",
              "calculation_explanation":"ce"
            }
          }
        }
        """;

        ObjectMapper mapper = new ObjectMapper();

        AstrologyResponse resp = mapper.readValue(json, AstrologyResponse.class);
        assertNotNull(resp, "response should not be null");
        assertEquals("c1", resp.getClientId(), "clientId mismatch");
        assertNotNull(resp.getReport(), "report should not be null");

        String out = mapper.writeValueAsString(resp);
        assertTrue(out.contains("\"ideal_environment\":\"remote\""));
        assertTrue(out.contains("\"suggested_roles\""));
        assertTrue(out.contains("\"tips\""));
        assertTrue(out.contains("\"main_axes\":\"axisA | axisB\""));
        assertTrue(out.contains("\"paths\""));
        assertTrue(out.contains("\"sun\""));
        assertTrue(out.contains("\"moon\""));
        assertTrue(out.contains("\"ascendant\""));
    }
}
