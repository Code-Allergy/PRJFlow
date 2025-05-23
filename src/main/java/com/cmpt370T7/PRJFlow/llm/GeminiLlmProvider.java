package com.cmpt370T7.PRJFlow.llm;

import com.cmpt370T7.PRJFlow.util.web.WebConnection;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionBuilder;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeminiLlmProvider implements LlmProvider {
    private static final Logger logger = LoggerFactory.getLogger(GeminiLlmProvider.class);

    private final String apiKey;
    private String model;
    private static final String BASE_URL = "https://generativelanguage.googleapis.com";
    private static final String DEFAULT_MODEL = "gemini-1.5-flash";

    public GeminiLlmProvider(String apiKey) {
        this.apiKey = apiKey;
        this.model = DEFAULT_MODEL;
    }

    public GeminiLlmProvider(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String getProvider() {
        return "Google Gemini";
    }

    @Override
    public List<String> getAllModels() {
        // For now, return a fixed list.
        // Dynamic fetching can be added later by querying:
        // BASE_URL + "/v1beta/models?key=" + apiKey
        return Arrays.asList("gemini-1.5-flash", "gemini-1.5-pro-latest", "gemini-1.0-pro");
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String getUrl() {
        return BASE_URL;
    }

    @Override
    public boolean isAvailable() {
        // Assuming if this provider is instantiated, it's intended to be available.
        // A more robust check could ping an API endpoint.
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String queryProvider(String query, boolean jsonMode) {
        if (!isAvailable()) {
            logger.warn("Gemini provider is not available (API key might be missing).");
            return null;
        }

        String endpoint = String.format("%s/v1beta/models/%s:generateContent?key=%s", BASE_URL, model, apiKey);
        logger.info("Querying Gemini endpoint: {}", endpoint);

        try {
            String jsonPayload = createJsonPayload(query, jsonMode);
            WebConnection webConnection = WebConnectionBuilder.create()
                    .setUrl(endpoint)
                    .setMethod(WebConnectionMethod.POST)
                    .setJson(jsonPayload)
                    .setContentType("application/json")
                    .build();

            if (webConnection.connect()) {
                String response = webConnection.getResponse();
                logger.debug("Gemini API Response: {}", response);
                return extractResponse(response);
            } else {
                logger.error("Error: Unable to connect to Gemini API. Response code: {}, Message: {}",
                             webConnection.getResponseCode(), webConnection.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            logger.error("Error creating JSON payload or during API call for Gemini", e);
            return null;
        } catch (Exception e) {
            logger.error("Error extracting response from Gemini", e);
            return null;
        }
    }

    private String createJsonPayload(String query, boolean jsonMode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> part = new HashMap<>();
        part.put("text", query);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", Collections.singletonList(content));

        // TODO: Check Gemini documentation for how to enable JSON mode.
        // This is a placeholder and might not be correct.
        if (jsonMode) {
            // Example: "generationConfig": {"response_mime_type": "application/json"}
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("response_mime_type", "application/json");
            payload.put("generationConfig", generationConfig);
            logger.info("JSON mode requested for Gemini. Added generationConfig.");
        }

        return mapper.writeValueAsString(payload);
    }

    private String extractResponse(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);

        if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && rootNode.get("candidates").size() > 0) {
            JsonNode firstCandidate = rootNode.get("candidates").get(0);
            if (firstCandidate.has("content") && firstCandidate.get("content").has("parts") &&
                firstCandidate.get("content").get("parts").isArray() && firstCandidate.get("content").get("parts").size() > 0) {
                JsonNode firstPart = firstCandidate.get("content").get("parts").get(0);
                if (firstPart.has("text")) {
                    return firstPart.get("text").asText();
                }
            }
        }
        // Check for errors reported by the API
        if (rootNode.has("error")) {
            JsonNode errorNode = rootNode.get("error");
            String errorMessage = errorNode.has("message") ? errorNode.get("message").asText() : "Unknown API error";
            logger.error("Gemini API returned an error: {}", errorMessage);
            throw new Exception("Gemini API error: " + errorMessage);
        }

        logger.warn("Could not extract text from Gemini response: {}", jsonResponse);
        return null;
    }
}
