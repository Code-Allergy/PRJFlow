package com.cmpt370T7.PRJFlow.llm;

import com.cmpt370T7.PRJFlow.util.web.WebConnection;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionBuilder;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;



public class CloudLlmProvider implements LlmProvider {
    private static final Logger logger = LoggerFactory.getLogger(CloudLlmProvider.class);

    private final String provider;
    private final String provider_baseurl;
    private final String provider_endpoint;
    private final String apiKey;
    private String model;
    private float temperature = 0.2f;

    /// Only tested with Groq Cloud API
    public static class ChatCompletionResponse {
        public String id;
        public String object;
        public long created;
        public String model;
        public List<Choice> choices;
        public Usage usage;
        public String system_fingerprint;

        @JsonProperty("x_groq")
        public XGroq xGroq;

        public static class Choice {
            public long index;
            public boolean logprobs;
            public String finish_reason;
            public Message message;

            public static class Message {
                public String role;
                public String content;
            }
        }

        public static class Usage {
            public double queue_time;
            public long prompt_tokens;
            public double prompt_time;
            public long completion_tokens;
            public double completion_time;
            public long total_tokens;
            public double total_time;
        }

        public static class XGroq {
            public String id;
        }
    }


    // Constructor that takes provider details
    public CloudLlmProvider(String provider, String baseUrl, String apiKey, String defaultModel) {
        this.provider = provider;
        this.provider_baseurl = baseUrl;
        this.provider_endpoint = baseUrl + "/v1/chat/completions";
        this.apiKey = apiKey;
        this.model = defaultModel;
    }

    public static CloudLlmProvider createGroqProvider(String apiKey) {
        return new CloudLlmProvider(
                "Groq Cloud",
                "https://api.groq.com/openai",
                apiKey,
                "llama-3.1-8b-instant"
        );
    }

    public static CloudLlmProvider createOpenAIProvider(String apiKey) {
        return new CloudLlmProvider(
                "OpenAI",
                "https://api.openai.com",
                apiKey,
                "gpt-3.5-turbo"
        );
    }

    @Override
    public String getProvider() {
        return provider;
    }


    @Override
    public String getModel() {
        return model;
    }

    @Override
    public List<String> getAllModels() {
        String response = "";
        try {
            WebConnection webConnection = WebConnectionBuilder.create()
                    .setUrl(provider_baseurl + "/v1/models")
                    .setMethod(WebConnectionMethod.GET)
                    .setAuthorization("Bearer " + apiKey)
                    .setContentType("application/json")
                    .build();

            if (webConnection.connect()) {
                response = webConnection.getResponse();
            }
        } catch (IOException e) {
            logger.error("Failed to get models from Groq", e);
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data");

            List<String> modelIds = new ArrayList<>();
            Iterator<JsonNode> elements = dataNode.elements();
            while (elements.hasNext()) {
                JsonNode modelNode = elements.next();
                String id = modelNode.path("id").asText();
                modelIds.add(id);
            }

            return modelIds;
        } catch (IOException e) {
            logger.error("Failed to parse models from Groq", e);
            return null;
        }
    }

    @Override
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String getUrl() {
        return provider_endpoint;
    }

    @Override
    public String queryProvider(String query, boolean jsonMode) {
        StringBuilder response = new StringBuilder();
        try {
            WebConnection webConnection = WebConnectionBuilder.create()
                    .setUrl(provider_endpoint)
                    .setMethod(WebConnectionMethod.POST)
                    .setJson(createJsonPayload(query, jsonMode))
                    .setAuthorization("Bearer " + apiKey)
                    .build();

            if (webConnection.connect()) {
                response.append(webConnection.getResponse());
            } else {
                logger.error("Error: Unable to connect to Groq API");
                return null;
            }

        } catch (IOException e) {
            logger.error("Error: Unable to connect to Groq API", e);
        }

        try {
            return extractResponse(response.toString());
        } catch (Exception e) {
            logger.error("Error: Unable to extract response", e);
        }

        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    private String createJsonPayload(String query, boolean jsonMode) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", query);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messages", new Object[] {message});
        payload.put("model", model);
        payload.put("temperature", temperature);
        if (jsonMode) {
            payload.put("response_format", "{ \"type\": \"json_object\" }");
        }

        return mapper.writeValueAsString(payload);
    }

    private String extractResponse(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse response = mapper.readValue(jsonResponse, ChatCompletionResponse.class);
        return response.choices.getFirst().message.content;
    }

}
