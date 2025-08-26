package com.diakogiannis.nostradamus.astroapi_service.western.services;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.diakogiannis.nostradamus.astroapi_service.enums.ApiClient;
import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyRequest;
import com.diakogiannis.nostradamus.astroapi_service.western.dto.AstrologyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

@Service
@Slf4j
public class AstrologyPredictionService {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;

    public AstrologyPredictionService(
            WebClient.Builder builder,
            @Value("${openrouter.api.key}") String apiKey,
            @Value("${openrouter.api.url}") String apiUrl,
            @Value("${openrouter.model}") String model
    ) {
        this.model = model;
        this.webClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "https://jee.gr")
                .defaultHeader("X-Title", "Nostradamus western Astrology Query")
                .build();
    }

    private String sanitizeSvgField(String json) {
        Pattern pattern = Pattern.compile("\"chart_svg\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String originalSvg = matcher.group(1);

            // Escaping:
            String escapedSvg = originalSvg
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "")
                    .replace("\t", "\\t");

            matcher.appendReplacement(sb, "\"chart_svg\":\"" + escapedSvg + "\"");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }



    public Flux<AstrologyResponse> callOpenRouter(AstrologyRequest request) {
        String prompt = buildPrompt(request);

        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(message),
                "temperature", 0.7,
                "stream", true
        );

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> chunk.startsWith("data:") ? chunk.substring(5).trim() : chunk.trim())
                .filter(chunk -> !chunk.isBlank() && !chunk.equals("[DONE]"))
                .handle(new BiConsumer<String, SynchronousSink<AstrologyResponse>>() {
                    private final StringBuilder combinedContent = new StringBuilder();

                    @Override
                    public void accept(String chunk, SynchronousSink<AstrologyResponse> sink) {
                        try {
                            Map<String, Object> map = mapper.readValue(chunk, Map.class);
                            List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");

                            if (choices != null && !choices.isEmpty()) {
                                Map<String, Object> choice = choices.get(0);
                                Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                                String content = delta != null ? (String) delta.get("content") : null;

                                if (content != null) {
                                    log.debug("✏️ Accumulating chunk: {}", content);
                                    combinedContent.append(content);
                                }

                                Object finishReason = choice.get("finish_reason");
                                if (finishReason != null && !"null".equals(finishReason.toString())) {
                                    String fullText = combinedContent.toString();
                                    log.debug("✅ Finished stream. Attempting to parse full content.");

                                    // Extract JSON block (from { to })
                                    int start = fullText.indexOf('{');
                                    int end = fullText.lastIndexOf('}');
                                    if (start >= 0 && end > start) {
                                        String json = fullText.substring(start, end + 1);
                                        // Escape only chart_svg content if raw newlines detected
                                        if (json.contains("\"chart_svg\"")) {
                                            json = sanitizeSvgField(json);
                                        }

                                        try {
                                            AstrologyResponse.AstrologyReport report =
                                                    mapper.readValue(json, AstrologyResponse.AstrologyReport.class);
                                            AstrologyResponse response = new AstrologyResponse();
                                            response.setApiClient(ApiClient.GEMINI);
                                            response.setClientId("foo");
                                            response.setReport(report);

                                            sink.next(response);
                                            sink.complete();
                                            log.debug("JSON response: {}", json);
                                            log.info("Api response completed for {}", request.toString());
                                        } catch (Exception jsonEx) {
                                            log.error("❌ Failed to parse JSON from accumulated stream", jsonEx);
                                            sink.error(jsonEx);
                                        }
                                    } else {
                                        log.error("❌ No JSON structure found in response text.");
                                        sink.error(new RuntimeException("No JSON block found in response"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("❌ Error processing chunk", e);
                            sink.error(e);
                        } finally {
                            //nothing here
                        }
                    }
                })
                .onErrorResume(e -> {
                    log.error("❌ Streaming Gemini call failed", e);
                    return Flux.error(new RuntimeException("Astrology prediction failed: " + e.getMessage(), e));
                });
    }

    private String buildPrompt(AstrologyRequest request) {
        return """
                
                ### Τεχνικό Παράρτημα – Αστρονομικοί Υπολογισμοί
                
                1. Ιουλιανή Ημερομηνία (JD)
                JD = 367·Y
                     − floor[ 7·(Y + floor((M + 9) / 12)) / 4 ]
                     + floor[ 275·M / 9 ]
                     + D
                     + 1721013.5
                     + UT / 24
                  όπου:
                  • Y = έτος (π.χ. 1979)
                  • M = μήνας 1-12 (αν M ≤ 2 θέσε Y ← Y − 1, M ← M + 12)
                  • D = ημέρα (δεκαδικό)
                  • UT = καθολικός χρόνος σε ώρες (0 ≤ UT < 24)
                
                2. Μετατροπή σε Δυναμικό Χρόνο Βαρυτικού Κέντρου (TDB)
                T   = (JD − 2451545.0) / 36525
                ΔT  ≈ 69.184 + 0.0026·(Y − 2000)          (προσεγγιστικό)
                TDB = JD + ΔT / 86400
                
                3. Γκρίνουιτς Αστρικός Χρόνος (GST) και Τοπικός (LST)
                GST = 280.46061837
                      + 360.98564736629·(JD − 2451545.0)
                      + 0.000387933·T²
                      − T³ / 38710000
                
                LST = GST + λ
                  όπου λ = γεωγραφικό μήκος (ανατολικά θετικό)
                
                4. Ωροσκόπος (ASC)
                tan α = (sin LST·cos ε − tan φ·sin ε) / cos LST
                  όπου:
                  • φ = γεωγραφικό πλάτος
                  • ε = λόξωση εκλειπτικής
                
                5. Οίκοι Placidus
                tan θ2,11 = (1/2)·tan(LST ± 30°)
                tan θ3,10 = (1/3)·tan(LST ± 60°)
                  … (ίδιο μοτίβο για τους υπόλοιπους διαμερισμούς)
                
                6. Πλανητικές Συντεταγμένες (VSOP87)
                L(t) = Σₙ [ Aₙ·cos φₙ + Bₙ·sin φₙ ]
                φₙ = aₙ + bₙ·t
                  υπολογίζουμε το ηλιοκεντρικό διάνυσμα r_plan
                  και της Γης r_earth,
                  έπειτα το γεωκεντρικό r_geo = r_plan − r_earth,
                  πριν από την περιστροφή κατά ε
                
                7. Όψεις και Orb Threshold
                Δλ = |λ_A − λ_B| mod 360°
                Όψη υπάρχει όταν |Δλ − Φ| ≤ δ,
                  με Φ ∈ {0°, 60°, 90°, 120°, 180°}
                  και τυπικό δ = 6° για προσωπικούς πλανήτες
                
                Είσαι επαγγελματίας αστρολόγος. Σου δίνω στοιχεία γέννησης και θέλω να δημιουργήσεις ένα ΠΛΗΡΕΣ αστρολογικό report σε μορφή **JSON**. Χρησιμοποίησε δυτική αστρολογία και απάντησε επαγγελματικά ΣΤΑ ΕΛΛΗΝΙΚΑ.
                
                **ΣΗΜΑΝΤΙΚΕΣ ΟΔΗΓΙΕΣ - ΑΥΣΤΗΡΗ ΣΥΜΜΟΡΦΩΣΗ ΣΤΟ ΣΧΗΜΑ!**
                - Το παραγόμενο JSON **πρέπει να τηρεί ΑΚΡΙΒΩΣ** τη δομή που ακολουθεί.
                - **ΜΗΝ** προσθέτεις επιπλέον πεδία ή πληροφορίες που δεν προβλέπονται στο σχήμα.
                - Αν ένα πεδίο δεν έχει τιμή, ΔΕΝ ΤΟ ΠΕΡΙΛΑΜΒΑΝΕΙΣ καθόλου στο JSON (ή βάζεις `null` μόνο αν απαιτείται).
                - **ΜΗΝ** εισαγάγεις πεδία όπως "planets" μέσα σε άλλα sections (π.χ. aspect), ούτε καν σχόλια, markdown, ή επιπλέον metadata.
                - Τα πεδία θα έχουν μορφοποιηση html με bootstrap και οχι markup 
                - Σε κάθε υποπεδίο πχ sun κάτω απο το πεδίο planets υπάρχει το πεδίο technical_interpretation. Εκεί χρειάζομαι την καθαρά επιστημονική μεθοδολογία και υπολογισμούς που σε οδήγησε να εξάξει το συμπέρασμα οτι πχ ο Ήλιος είναι στους διδυμους. Θέλω αστρονομικά και μαθηματικά στοιχεία αναλυτικά.
                - Τα κείμενα των πεδίων μπορούν να περιέχουν html με bootstrap, όχι markdown.
                
                
                ---
                
                **ΠΕΔΙΟ calculation_explanation**
                Το πεδίο "calculation_explanation" στο "ascendant" πρέπει να περιέχει αναλυτική εξήγηση του πώς υπολογίστηκε ο ωροσκόπος, συμπεριλαμβανομένων:
                - Της επίδρασης του γεωγραφικού πλάτους στη διάρκεια ανατολής των ζωδίων
                - Των χρονικών παραθύρων ανατολής για το συγκεκριμένο πλάτος
                - Της αιτιολογίας γιατί το συγκεκριμένο ζώδιο ανέτειλε εκείνη την ώρα
                - Οποιαδήποτε άλλη τεχνική λεπτομέρεια που επηρεάζει τον υπολογισμό του ωροσκόπου
                
                ---
                **ΠΕΔΙΟ chart_svg**
                Το πεδίο "chart_svg" ΠΡΕΠΕΙ να περιέχει ΟΛΟ τον SVG κώδικα του γενέθλιου χάρτη, έτοιμο για εμφάνιση, χωρίς καθόλου placeholder ή σχόλιο *ΑΛΛΑ ΠΡΟΣΟΧΗ ΝΑ ΜΗΝ ΕΧΕΙ NEWLINES Ή ΟΤΙΔΗΠΟΤΕ ΑΛΛΟ ΜΠΟΡΕΙ ΝΑ ΣΠΑΣΕΙ ΤΗ ΔΟΜΗ ΤΟΥ JSON. ΠΡΕΠΕΙ ΠΑΝΤΑ ΤΟ JSON ΝΑ ΕΙΝΑΙ ΕΓΚΥΡΟ*
                
                ---
                
                Πεδίο year_prediction
                - Εδώ θα βάλεις πλήρη και αναλυτική πρόβλεψη για το έτος μήνα προς μήνα χρησημοποιόντας αποκλειστικα Θα πρέπει να είναι εκτενής και υποστηριγμένη απο πλανιτικά και μαθηματικά δεδομένα ΓΙΑ ΚΑΘΕ ΜΗΝΑ. πχ Σεπτέμβριος - Οκτώβριος Δημιουργικοτητα και σχεσεις. Το Σεπτεβριο θα γνωρισετε τον Έρωτα επειδη μπλα μπλα μπλα... Και αυτο φαινεται γιατι η αφροδητη ειναι στη 12η μοιρα κλπ κλπ... ΠΡΟΣΟΧΗ ΔΕΝ ΘΕΛΩ DROP DOWN ΑΛΛΑ ΛΙΣΤΑ ΜΗΝΑ ΚΑΙ ΠΡΟΒΛΕΨΗ ΤΟΥ ΚΑΘΕ ΜΗΝΑ ΣΕ ΚΟΥΤΙΑ 
                
                ---
                *ΣΗΜΕΙΩΣΕΙΣ*
                
                - Επέστρεψε μόνο καθαρό JSON, ποτέ σχόλια ή markdown.
                - Τα αριθμητικά πεδία να δίνονται χωρίς σύμβολα (π.χ. 23.4 όχι "23°").
                - To SVG πρέπει να μπαίνει πάντα πλήρες στο chart_svg χωρίς newlines.
                
                ---
                **ΠΡΕΠΕΙ ΠΑΝΤΑ ΤΟ JSON ΝΑ ΕΙΝΑΙ ΕΓΚΥΡΟ ΟΠΟΤΕ ΤΟ ΕΛΕΓΧΕΙΣ ΣΤΟ ΤΕΛΟΣ ΚΑΙ ΦΡΟΝΤΙΖΕΙΣ ΘΕΜΑΤΑ ΟΠΩς NEWLINES ΣΤΟ SVG Ή ΟΤΙΔΗΠΟΤΕ ΑΛΛΟ ΜΠΟΡΕΊ ΝΑ ΣΠΆΣΕΙ ΤΗ ΔΟΜΗ ΤΟΥ**
                
                
                **ΔΟΜΗ ΕΞΟΔΟΥ JSON:**
                
                {
                "birth_data": {
                "date": "YYYY-MM-DD",
                "time": "HH:MM",
                "location": "Full address or City, Country",
                "coordinates": {"latitude": "...", "longitude": "..."}
                "zodiac_sign": "..."
                },
                "ascendant": {
                "sign": "...",
                "degree": "...",
                "interpretation": "...",
                "calculation_explanation": "..."
                },
                "planets": {
                "Sun": {"sign": "...", "degree": "...", "house": ..., "interpretation": "...", "technical_interpretation": "..."},
                "...": {...}
                },
                "aspects": [
                {"between": ["Sun", "Mars"], "type": "Square", "orb": ..., "interpretation": "..."}
                ],
                "personality_summary": "...",
                "yearly_forecast": {
                "year": ...,
                "summary": "...",
                "important_transits": ["...", "..."],
                "tips": ["...", "..."]
                },
                "eclipses_lucky_windows": {
                "year": ...,
                "solar_eclipses": ["...", "..."],
                "lunar_eclipses": ["...", "..."],
                "lucky_windows": ["...", "..."],
                "advice": "..."
                },
                "career_guidance": {
                "main_axes": "...",
                "paths": ["...", "..."],
                "ideal_environment": "...",
                "tips": ["...", "..."],
                "suggested_roles": ["...", "..."],
                "final_summary": "..."
                },
                "chart_svg": "<svg xmlns='http://www.w3.org/2000/svg' width='600' height='600'>...</svg>",
                "final_overview": "...",
                "year_prediction": "..."
                }
                
                ---
                
                **ΞΕΚΙΝΗΣΕ ΚΑΙ ΕΠΙΣΤΡΕΨΕ ΜΟΝΟ ΤΟ JSON. ΜΗΝ ΠΡΟΣΘΕΣΕΙΣ ΚΑΝΕΝΑ ΠΕΡΙΤΤΟ ΠΕΔΙΟ, ΣΧΟΛΙΟ Ή ΠΕΡΙΤΤΗ ΠΛΗΡΟΦΟΡΙΑ.**
                
                ---
                
                *(Προαιρετικά, αν κάποια πεδία όπως βαθμοί είναι αριθμοί, να δίνονται χωρίς σύμβολα π.χ. "23.4" και ΟΧΙ "23°".)*
                
                ---
                                
                Πριν ξεκινήσεις τον υπολογισμό:
                - Από τη διεύθυνση που παρέχω (πόλη, χώρα, ή/και πιο ακριβής τοποθεσία), υπολόγισε γεωγραφικό πλάτος και μήκος.
                - Αν δεν μπορείς να βρεις τη διεύθυνση με ακρίβεια, επίλεξε το πλησιέστερο δυνατό σημείο.
                - Συμπεριέλαβε τις συντεταγμένες στο πεδίο `"coordinates"` του JSON.

                Το αποτέλεσμα πρέπει να περιλαμβάνει:
                - Γενέθλια στοιχεία και γεωγραφικές συντεταγμένες.
                - Πλήρη ανάλυση πλανητικών θέσεων σε ζώδια & οίκους.
                - Όψεις.
                - Αναλυτικές ερμηνείες προσωπικότητας.
                - Εκλείψεις & “τυχερά παράθυρα” για το %d.
                - Γενική πρόβλεψη του %d.
                - Ωροσκόπο.
                - Επαγγελματικές συμβουλές, όπως:
                  - Κύριοι άξονες επαγγελματικής κλίσης
                  - Πιθανές επαγγελματικές διαδρομές
                  - Ιδανικό εργασιακό περιβάλλον
                  - Συμβουλές καριέρας
                  - Ενδεικτικές καριέρες/ρόλοι
                  - Τελικό συμπέρασμα για την επαγγελματική του πορεία
                - Χρήσιμες προσωπικές συμβουλές για το %d.
                - Τελική σύνοψη.
                - Ολόκληρο τον γενέθλιο χάρτη σε **SVG**, ενσωματωμένο στο πεδίο `"chart_svg"`.

                **ΓΕΝΝΕΘΛΙΟΣ ΧΑΡΤΗΣ ΠΕΔΙΟ chart_svg**
                Ο ΓΕΝΝΕΘΛΙΟΣ ΧΑΡΤΗΣ ΘΑ ΠΡΕΠΕΙ ΠΑΝΤΑ ΝΑ ΕΙΝΑΙ ΟΛΟΚΛΗΡΟΣ!!!! ΔΕΝ ΘΕΛΩ ΝΑ ΜΟΥ ΓΡΑΦΕΙΣ ΠΡΑΓΜΑΤΑ ΤΥΠΟΥ "<!-- ΠΡΟΣΟΧΗ: Αυτό είναι ένα placeholder. ΣΤΟ chart_svg θα έπρεπε να είναι ΟΛΟΚΛΗΡΟΣ ο SVG κώδικας του γενέθλιου χάρτη. -->" ΘΕΛΩ ΟΛΟΚΛΗΡΟ ΤΟ ΠΕΡΙΕΧΟΜΕΝΟ ΤΟΥ SVG ΕΤΟΙΜΟ ΓΙΑ ΠΡΟΒΟΛΗ`. Ο χάρτης πρεπει να ειναι οσο το δυνατόν πιο αναλυτικος
            
                ---
                **ΓΕΝΝΗΤΡΙΑ JSON***
             The JSON generator. When given a description of data, output only valid JSON (no explanation, no extra text) representing that data. Requirements:
                    - All string values must conform to the JSON specification: any control character (U+0000 through U+001F) must be escaped.
                       - Use \\n for newline, \\r for carriage return, \\t for tab, \\b for backspace, \\f for form feed.
                       - Any other control character in that range must be escaped as \\u00xx (hex).
                       - Escape double quotes as \\" and backslashes as \\\\.
                    - Do not emit literal unescaped newlines, tabs, or other control chars inside string values.
                    - Use ISO 8601 format for any timestamps.
                    - Output pretty-printed JSON with 2-space indentation unless the user asks for compact.
                    - If the input includes raw multi-line or unescaped text, automatically sanitize/escape it to satisfy the above rules.
                
                   ⚠️ WRONG OUTPUT:
                   "chart_svg": "<svg>\\n<line x1=\\"0\\" x2=\\"1\\" /></svg>" ❌
                   
                   ✅ CORRECT OUTPUT:
                   "chart_svg": "<svg>\\\\n<line x1=\\\\\\"0\\\\\\" x2=\\\\\\"1\\\\\\" />\\\\n</svg>"
                
                ---
                
                **ΔΕΔΟΜΕΝΑ ΕΙΣΟΔΟΥ:**
                Έτος πρόβλεψης: %d 
                Ημερομηνία γέννησης: %s 
                Ώρα γέννησης: %s 
                Διεύθυνση: %s 
                Φύλο: %s 


                """
                .formatted(
                        request.yearOfForecast(),
                        request.yearOfForecast(),
                        request.yearOfForecast(),
                        request.yearOfForecast(),
                        request.birthDate(),
                        request.birthTime(),
                        request.address(),
                        request.sex());
    }

}
