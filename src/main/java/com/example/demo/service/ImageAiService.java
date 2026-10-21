package com.example.demo.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ImageAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public ImageAiService() {

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    // =========================================================
    // DETECT CATEGORY FROM IMAGE
    // =========================================================

    public String detectCategory(byte[] imageBytes, String mimeType) {

        try {

            String base64Image =
                    Base64.getEncoder()
                            .encodeToString(imageBytes);

            // IMAGE DATA
            Map<String, Object> inlineData =
                    new HashMap<>();

            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart =
                    new HashMap<>();

            imagePart.put("inline_data", inlineData);

            // AI PROMPT
            Map<String, Object> textPart =
                    new HashMap<>();

            textPart.put(
                    "text",
                    """
                    You are an AI civic issue classifier.

                    Look ONLY at the uploaded image and select
                    exactly ONE category.

                    Allowed categories:

                    Street Light
                    Garbage Issue
                    Water Leakage
                    Drainage
                    Road Issue
                    Traffic Signal
                    Other

                    IMPORTANT:

                    If the image contains a drainage grate,
                    storm drain, roadside drain, gutter,
                    sewer drain, blocked drain, or leaves/dirt
                    covering a drainage opening, classify it as:

                    Drainage

                    If the image clearly shows water leaking
                    from a broken water pipe, water supply pipe,
                    pipeline, tap, or pipe joint, classify it as:

                    Water Leakage

                    A drainage grate with leaves or dirt is
                    Drainage, NOT Water Leakage.

                    Do not classify an image as Water Leakage
                    just because water or wetness may be present.

                    If a pothole or damaged road is the main issue:
                    Road Issue

                    If garbage or waste is the main issue:
                    Garbage Issue

                    If a street light or street lamp is damaged:
                    Street Light

                    If a traffic signal or traffic light is damaged:
                    Traffic Signal

                    Return ONLY ONE of the exact category names.
                    Do not explain.
                    Do not return multiple categories.
                    """
            );

            // CONTENT
            Map<String, Object> content =
                    new HashMap<>();

            content.put(
                    "parts",
                    List.of(
                            textPart,
                            imagePart
                    )
            );

            // REQUEST BODY
            Map<String, Object> requestBody =
                    new HashMap<>();

            requestBody.put(
                    "contents",
                    List.of(content)
            );

            // GENERATION CONFIG
            Map<String, Object> generationConfig =
                    new HashMap<>();

            generationConfig.put(
                    "temperature",
                    0
            );

            requestBody.put(
                    "generationConfig",
                    generationConfig
            );

            // =====================================================
            // GEMINI IMAGE API CALL
            // =====================================================

            String response =
                    restClient.post()

                            .uri(
                                    "/v1beta/models/gemini-2.5-flash:generateContent"
                            )

                            .header(
                                    "x-goog-api-key",
                                    apiKey
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .body(requestBody)

                            .retrieve()

                            .body(String.class);

            System.out.println(
                    "Gemini Image Response:"
            );

            System.out.println(response);

            // EXTRACT CATEGORY
            String category =
                    extractCategory(response);

            System.out.println(
                    "Detected AI Category: "
                    + category
            );

            return category;

        } catch (Exception e) {

            System.out.println(
                    "Image AI Error: "
                    + e.getMessage()
            );

            return "Other";
        }
    }


    // =========================================================
    // GENERATE AI COMPLAINT SUMMARY
    // =========================================================

    public String generateSummary(String description) {

        try {

            if (description == null
                    || description.trim().isEmpty()) {

                return "No description provided";
            }

            // AI PROMPT
            Map<String, Object> textPart =
                    new HashMap<>();

            textPart.put(
                    "text",
                    """
                    You are an AI assistant for a civic complaint system.

                    Create a short and clear summary of the citizen's complaint.

                    Rules:

                    - Keep the important issue.
                    - Keep the affected problem or location if mentioned.
                    - Do not add information that the citizen did not provide.
                    - Do not change the meaning.
                    - Use one simple sentence.
                    - Maximum 25 words.
                    - Return ONLY the summary.

                    Citizen Complaint:
                    """
                    + description
            );

            // CONTENT
            Map<String, Object> content =
                    new HashMap<>();

            content.put(
                    "parts",
                    List.of(textPart)
            );

            // REQUEST BODY
            Map<String, Object> requestBody =
                    new HashMap<>();

            requestBody.put(
                    "contents",
                    List.of(content)
            );

            // GENERATION CONFIG
            Map<String, Object> generationConfig =
                    new HashMap<>();

            generationConfig.put(
                    "temperature",
                    0
            );

            requestBody.put(
                    "generationConfig",
                    generationConfig
            );

            // =====================================================
            // GEMINI SUMMARY API CALL
            // =====================================================

            String response =
                    restClient.post()

                            .uri(
                                    "/v1beta/models/gemini-2.5-flash:generateContent"
                            )

                            .header(
                                    "x-goog-api-key",
                                    apiKey
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .body(requestBody)

                            .retrieve()

                            .body(String.class);

            System.out.println(
                    "Gemini Summary Response:"
            );

            System.out.println(response);

            // EXTRACT SUMMARY
            String summary =
                    extractSummary(response);

            System.out.println(
                    "AI Summary: "
                    + summary
            );

            return summary;

        } catch (Exception e) {

            System.out.println(
                    "AI Summary Error: "
                    + e.getMessage()
            );

            // Keep original description if AI fails
            return description;
        }
    }


    // =========================================================
    // EXTRACT CATEGORY
    // =========================================================

    private String extractCategory(String response) {

        if (response == null) {
            return "Other";
        }

        String text =
                response.toLowerCase()
                        .trim();

        // =====================================================
        // DRAINAGE
        // =====================================================

        if (text.contains("drainage")
                || text.contains("storm drain")
                || text.contains("drain grate")
                || text.contains("drainage grate")
                || text.contains("roadside drain")
                || text.contains("sewer drain")
                || text.contains("blocked drain")
                || text.contains("gutter")) {

            return "Drainage";
        }

        // =====================================================
        // STREET LIGHT
        // =====================================================

        if (text.contains("street light")
                || text.contains("street lamp")) {

            return "Street Light";
        }

        // =====================================================
        // GARBAGE
        // =====================================================

        if (text.contains("garbage issue")
                || text.contains("garbage")
                || text.contains("waste")) {

            return "Garbage Issue";
        }

        // =====================================================
        // WATER LEAKAGE
        // =====================================================

        if (text.contains("water leakage")
                || text.contains("water leak")
                || text.contains("leaking pipe")
                || text.contains("broken water pipe")
                || text.contains("water supply pipe")) {

            return "Water Leakage";
        }

        // =====================================================
        // TRAFFIC SIGNAL
        // =====================================================

        if (text.contains("traffic signal")
                || text.contains("traffic light")) {

            return "Traffic Signal";
        }

        // =====================================================
        // ROAD ISSUE
        // =====================================================

        if (text.contains("road issue")
                || text.contains("pothole")
                || text.contains("road damage")) {

            return "Road Issue";
        }

        return "Other";
    }


    // =========================================================
    // EXTRACT AI SUMMARY
    // =========================================================

    private String extractSummary(String response) {

        if (response == null
                || response.trim().isEmpty()) {

            return "Summary not available";
        }

        try {

            String marker =
                    "\"text\":\"";

            int start =
                    response.indexOf(marker);

            if (start == -1) {

                return "Summary not available";
            }

            start =
                    start + marker.length();

            int end =
                    response.indexOf(
                            "\"",
                            start
                    );

            if (end == -1) {

                return "Summary not available";
            }

            String summary =
                    response.substring(
                            start,
                            end
                    );

            summary =
                    summary.replace(
                            "\\n",
                            " "
                    )
                    .replace(
                            "\\\"",
                            "\""
                    )
                    .trim();

            if (summary.isEmpty()) {

                return "Summary not available";
            }

            return summary;

        } catch (Exception e) {

            return "Summary not available";
        }
    }
}