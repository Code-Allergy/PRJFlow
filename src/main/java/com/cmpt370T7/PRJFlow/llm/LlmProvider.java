package com.cmpt370T7.PRJFlow.llm;

import java.util.List;

public interface LlmProvider {
    /**
     * Get the provider name
     * @return The provider name
     */
    String getProvider();

    /**
     * Query the provider for all available models
     * @return A list of all the models available
     */
    List<String> getAllModels();

    /**
     * Get the model to use for the provider
     * @return The model to use
     */
    String getModel();

    /**
     * Set the model to use for the provider
     * @param model The model to use
     */
    void setModel(String model);

    /**
     * Get the URL of the provider
     * @return The URL of the provider
     */
    String getUrl();

    /**
     * Check if the provider is available
     * @return Whether the provider is available
     */
    boolean isAvailable();

    /**
     * Query the provider with the given query
     * @param query The query to send to the provider
     * @param jsonMode Whether the response should be in JSON mode
     * @return The response body from the provider
     */
    String queryProvider(String query, boolean jsonMode);
}
