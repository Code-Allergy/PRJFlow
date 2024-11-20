package com.cmpt370T7.PRJFlow.llm;

import com.cmpt370T7.PRJFlow.util.web.WebConnection;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionBuilder;
import com.cmpt370T7.PRJFlow.util.web.WebConnectionMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A provider implementation for integrating with Ollama LLM services.
 * This class handles communication with a locally running Ollama instance,
 * managing model selection, and executing queries against the Ollama API.
 * The provider requires Ollama to be installed and running locally on port 11434.
 * If Ollama is not installed or running, the class will throw appropriate exceptions.
 *
 * @see <a href="https://ollama.com">Ollama Official Website</a>
 */
public class OllamaProvider implements LlmProvider {
    private static final Logger logger = LoggerFactory.getLogger(OllamaProvider.class);

    private static final String OLLAMA_DEFAULT_MODEL = "mistral";
    private static final String OLLAMA_INSTALL_URL = "https://ollama.com/download";
    private static final String provider_baseurl = "http://localhost:11434";
    private static final String provider = "Ollama";
    private static final String provider_endpoint = provider_baseurl + "/api/generate";

    private String model;
    /**
     * Initializes a new OllamaProvider instance.
     * Verifies that Ollama is installed and running, and initializes the default model.
     *
     * @throws RuntimeException if Ollama is not installed, not running, or no models are available
     */
    public OllamaProvider() {
        if (!isOllamaInstalled()) {
            // TODO
            logger.error("Ollama is not installed. Please download and install Ollama from {}", OLLAMA_INSTALL_URL);
            return;
        }
        logger.debug("Ollama is installed and available on path!");

        if (!isOllamaRunning()) {
            // start ollama

            // for now, just error.
            logger.error("Ollama is not running. Please start Ollama before using it. Run `ollama serve` to start Ollama.");
            return;
        }

        List<String> models = getAllModels();
        if (models == null || models.isEmpty()) {
            // get default model

            // for now, just error.
            logger.error("No models available from Ollama. Please check if Ollama is running and has models available.");
            return;
        }

        // use the default model if its available, otherwise use the first model
        model = models.contains(OLLAMA_DEFAULT_MODEL) ? OLLAMA_DEFAULT_MODEL : models.getFirst();
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
        StringBuilder response = new StringBuilder();
        WebConnection webConnection = WebConnectionBuilder.create()
                .setUrl(provider_baseurl + "/api/tags")
                .setMethod(WebConnectionMethod.GET)
                .build();

        try {
            if (webConnection.connect()) {
                response.append(webConnection.getResponse());
            }
        } catch (IOException e) {
            logger.error("Failed to get models from Ollama", e);
            throw new RuntimeException(e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode modelsNode = rootNode.path("models");

            return StreamSupport.stream(modelsNode.spliterator(), false)
                    .map(model -> model.path("name").asText().split(":")[0])
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse models from Ollama", e);
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
        try {
            String response = executeQuery(query, jsonMode);
            return extractResponse(response);
        } catch (IOException e) {
            logger.error("Failed to execute query to provider", e);
            return null;
        } catch (Exception e) {
            logger.error("Failed to process provider response", e);
            return null;
        }
    }

    @Override
    public boolean isAvailable() {
        return isOllamaInstalled() && isOllamaRunning() && model != null;
    }

    /**
     * Executes a query against the Ollama API and returns the raw response.
     *
     * @param query the prompt to send to the model
     * @param jsonMode if true, requests JSON formatted output
     * @return String containing the raw API response
     * @throws IOException if the connection fails
     */
    private String executeQuery(String query, boolean jsonMode) throws IOException {
        WebConnection webConnection = WebConnectionBuilder.create()
                .setUrl(provider_endpoint)
                .setMethod(WebConnectionMethod.POST)
                .setJson(createOllamaJsonPayload(query, jsonMode))
                .build();

        if (webConnection.connect()) {
            return webConnection.getResponse();
        } else {
            throw new IOException("Failed to connect to provider");
        }
    }

    /**
     * Checks if Ollama is installed by executing the version command.
     *
     * @return true if Ollama is installed, false otherwise
     */
    static boolean isOllamaInstalled() {
        logger.debug("Checking if Ollama is installed...");
        AtomicBoolean versionFound = new AtomicBoolean(false);

        int exitCode = executeCommand("ollama --version", output -> {
            if (!output.isEmpty()) {
                logger.info("Ollama version: {}", output);
                versionFound.set(true);
            }
        });

        boolean isInstalled = exitCode == 0 && versionFound.get();
        logger.info("Ollama installed: {}", isInstalled);
        return isInstalled;
    }

    /**
     * Checks if the Ollama service is running by attempting to connect to it.
     *
     * @return true if Ollama is running and responding, false otherwise
     */
    private static boolean isOllamaRunning() {
        try {
            URL url = URI.create(provider_baseurl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getOllamaDefaultModel() {
        return OLLAMA_DEFAULT_MODEL;
    }

    public static void pullModel(String model) {
        logger.info("Starting to pull model: {}", model);
        executeCommand("ollama pull " + model, output -> {
            if (output.contains("downloading:")) {
                try {
                    String percentStr = output.split("%")[0];
                    percentStr = percentStr.substring(percentStr.lastIndexOf(" ")).trim();
                    double percent = Double.parseDouble(percentStr);
                    Platform.runLater(() -> {
                        // TODO update progress bar
                    });
                } catch (Exception e) {
                    logger.error("Error parsing progress", e);
                }
            }
        });
        logger.info("Finished pulling model: {}", model);
    }

    /**
     * Executes a system command and returns the exit code.
     *
     * @param command the command to execute
     * @return int representing the exit code (0 for success, negative for errors)
     */
    private static int executeCommand(String command, Consumer<String> outputHandler) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(isWindows() ? "cmd.exe" : "sh",
                    isWindows() ? "/c" : "-c",
                    command);
            processBuilder.redirectErrorStream(true);

            logger.debug("Executing command: {}", command);
            Process process = processBuilder.start();

            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("Command output: {}", line);
                        if (outputHandler != null) {
                            outputHandler.accept(line);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error reading process output", e);
                }
            });
            outputReader.start();

            int exitCode = process.waitFor();
            logger.debug("Command completed with exit code: {}", exitCode);
            return exitCode;

        } catch (IOException | InterruptedException e) {
            logger.error("Failed to execute command: {}", command, e);
            return -1;
        }
    }

    /**
     * Checks if the current operating system is Windows.
     *
     * @return true if running on Windows, false otherwise
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Creates the JSON payload for the Ollama API request.
     *
     * @param query the prompt to send to the model
     * @param jsonMode if true, requests JSON formatted output
     * @return String containing the JSON payload
     * @throws IOException if JSON serialization fails
     */
    private String createOllamaJsonPayload(String query, boolean jsonMode) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> message = new HashMap<>();
        message.put("model", model);
        message.put("prompt", query);
        message.put("stream", false);
        if (jsonMode) {
            message.put("format", "json");
        }

        return mapper.writeValueAsString(message);
    }

    /**
     * Extracts the response text from the Ollama API JSON response.
     *
     * @param response the raw JSON response from the API
     * @return String containing the extracted response text
     * @throws Exception if JSON parsing fails
     */
    private String extractResponse(String response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode response_field = rootNode.path("response");

        return response_field.asText();
    }
}
