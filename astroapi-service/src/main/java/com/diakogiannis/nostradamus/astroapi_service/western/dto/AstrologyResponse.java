package com.diakogiannis.nostradamus.astroapi_service.western.dto;

import com.diakogiannis.nostradamus.astroapi_service.dto.AbstractAstrologyResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AstrologyResponse extends AbstractAstrologyResponse {

    @JsonProperty("report")
    private AstrologyReport report;

    @Data
    @NoArgsConstructor
    @ToString(callSuper = true)
    public static class AstrologyReport {

        @JsonProperty("birth_data")
        private BirthData birthData;

        @JsonProperty("ascendant")
        private Ascendant ascendant;

        @JsonProperty("planets")
        private Map<String, Planet> planets;

        @JsonProperty("aspects")
        private List<Aspect> aspects;

        @JsonProperty("personality_summary")
        private String personalitySummary;

        @JsonProperty("yearly_forecast")
        private YearlyForecast yearlyForecast;

        @JsonProperty("eclipses_lucky_windows")
        private EclipsesLuckyWindows eclipsesLuckyWindows;

        @JsonProperty("career_guidance")
        private CareerGuidance careerGuidance;

        @JsonProperty("chart_svg")
        private String chartSvg;

        @JsonProperty("final_overview")
        private String finalOverview;

        @JsonProperty("year_prediction")
        private String yearPrediction;

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class BirthData {

            @JsonProperty("date")
            private String date;

            @JsonProperty("time")
            private String time;

            @JsonProperty("location")
            private String location;

            @JsonProperty("coordinates")
            private Coordinates coordinates;

            @JsonProperty("zodiac_sign")
            private String zodiacSign;

            @Data
            @NoArgsConstructor
            public static class Coordinates {
                @JsonProperty("latitude")
                private String latitude;

                @JsonProperty("longitude")
                private String longitude;
            }
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class Ascendant {
            @JsonProperty("sign")
            private String sign;

            @JsonProperty("degree")
            private String degree;

            @JsonProperty("interpretation")
            private String interpretation;

            @JsonProperty("calculation_explanation")
            private String calculationExplanation;
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class Planet {
            @JsonProperty("sign")
            private String sign;

            @JsonProperty("degree")
            private String degree;

            @JsonProperty("house")
            private String house;

            @JsonProperty("interpretation")
            private String interpretation;

            @JsonProperty("technical_interpretation")
            private String technicalInterpretation;
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class Aspect {
            @JsonProperty("between")
            private List<String> between;

            @JsonProperty("type")
            private String type;

            @JsonProperty("orb")
            private String orb;

            @JsonProperty("interpretation")
            private String interpretation;
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class YearlyForecast {
            @JsonProperty("year")
            private int year;

            @JsonProperty("summary")
            private String summary;

            @JsonProperty("important_transits")
            private List<String> importantTransits;

            @JsonProperty("tips")
            private List<String> tips;
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class EclipsesLuckyWindows {
            @JsonProperty("year")
            private int year;

            @JsonProperty("solar_eclipses")
            private List<String> solarEclipses;

            @JsonProperty("lunar_eclipses")
            private List<String> lunarEclipses;

            @JsonProperty("lucky_windows")
            private List<String> luckyWindows;

            @JsonProperty("advice")
            private String advice;
        }

        @Data
        @NoArgsConstructor
        @ToString(callSuper = true)
        public static class CareerGuidance {
            @JsonProperty("main_axes")
            private String mainAxes;

            @JsonProperty("paths")
            private List<String> paths;

            @JsonProperty("ideal_environment")
            private String idealEnvironment;

            @JsonProperty("tips")
            private List<String> tips;

            @JsonProperty("suggested_roles")
            private List<String> suggestedRoles;

            @JsonProperty("final_summary")
            private String finalSummary;
        }
    }
}
