package com.cmpt370T7.PRJFlow.llm;

import java.util.List;

public interface LlmProvider {
    /**
     * Get the provider name
     * @return The provider name
     */
    public String getProvider();

    /**
     * Query the provider for all available models
     * @return A list of all the models available
     */
    public List<String> getAllModels();

    /**
     * Get the model to use for the provider
     * @return The model to use
     */
    public String getModel();
    /**
     * Set the model to use for the provider
     * @param model The model to use
     */
    public void setModel(String model);

    /**
     * Get the URL of the provider
     * @return The URL of the provider
     */
    public String getUrl();

    /**
     * Check if the provider is available
     * @return Whether the provider is available
     */
    public boolean isAvailable();

    /**
     * Query the provider with the given query
     * @param query The query to send to the provider
     * @param jsonMode Whether the response should be in JSON mode
     * @return The response body from the provider
     */
    public String queryProvider(String query, boolean jsonMode);
}
