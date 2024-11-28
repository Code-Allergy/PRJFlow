package com.cmpt370T7.PRJFlow.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A singleton engine class that manages AI-powered text and CSV generation operations.
 * This class serves as a wrapper around an LLM (Large Language Model) provider,
 * offering specialized methods for generating text summaries and CSV data.
 * The class must be instantiated with a valid LLmProvider before use through
 * the {@link #instantiate(LlmProvider)} method. If no provider is supplied,
 * the engine will be disabled but won't throw exceptions during instantiation.
 */
public class AiEngine {
    private static final Logger logger = LoggerFactory.getLogger(AiEngine.class);

    private final LlmProvider provider;
    private static AiEngine aiEngine;
    private boolean enabled = true;

    /**
     * Prompt template for generating CSV data from input text.
     * The prompt instructs the LLM to extract key information and format it as CSV,
     * with properly aligned columns and rows for human readability.
     */
    private static final String CSV_GENERATOR_PROMPT = """
    Generate a detailed CSV format containing key information extracted from the following data. 
    Do not speak or add anything unnecessary. 
    Output only the CSV data surrounded by curly braces {}. 
    Ensure that the columns and rows are well-aligned for readability and clarity.
    Include all relevant key information that a human would find useful, and make sure the CSV is 
    properly formatted with headers where applicable. Organize the data logically, ensuring consistency 
    in the columns and rows to best represent the data in a structured and clear manner.
""";



    /**
     * Prompt template for generating text summaries from input text.
     * The prompt instructs the LLM to create a concise summary without any
     * additional commentary or formatting.
     */
    private static final String TEXT_GENERATOR_PROMPT = """
    Generate a detailed, comprehensive summary of the following input data.  
    Do not speak or add anything unnecessary.  
    Do not say 'here is your summary', just provide the summary.
    Include as much relevant detail as possible while ensuring clarity and coherence but keep it succinct.  
    Your summary should highlight the key points, provide context, and
    explain the most important aspects of the input data in a well-structured manner.
    Make sure the summary is thorough but concise enough to be easily understood.
    Avoid omitting important information.
""";

    /**
     * Private constructor for the singleton pattern.
     * Initializes the engine with the provided LLM provider.
     *
     * @param provider The LLM provider to use for text generation.
     *                If null, the engine will be created in a disabled state.
     */
    private AiEngine(LlmProvider provider) {
        if (provider == null) {
            this.enabled = false;
            logger.warn("No provider was provided, defaulting to no provider");
        }
        this.provider = provider;
    }

    /**
     * Instantiates the AiEngine singleton with the specified provider.
     * This method must be called before getInstance().
     *
     * @param provider The LLM provider to use for text generation
     * @throws IllegalStateException if the engine has already been instantiated
     */
    public static void instantiate(LlmProvider provider) {
        if (aiEngine != null) {
            throw new IllegalStateException("AiEngine has already been instantiated");
        }
        aiEngine = new AiEngine(provider);
    }

    /**
     * Returns the singleton instance of AiEngine.
     *
     * @return The singleton AiEngine instance
     * @throws IllegalStateException if the engine hasn't been instantiated via instantiate()
     */
    public static AiEngine getInstance() {
        if (aiEngine == null) {
            throw new IllegalStateException("AiEngine has not been instantiated");
        }
        return aiEngine;
    }

    /**
     * Checks if the AiEngine is enabled and ready to process requests.
     * The engine is disabled if it was initialized with a null provider.
     *
     * @return true if the engine is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Generates a text summary of the provided input text using the LLM provider.
     *
     * @param body The input text to summarize
     * @return A generated summary of the input text, or null if the provider fails
     *         or the engine is disabled
     */
    public String createTextSummary(String body) {
        String prompt = TEXT_GENERATOR_PROMPT + body;
        logger.info("Prompting LLM for Text summary: \nSystem: {}\nBody: {}", TEXT_GENERATOR_PROMPT, body);
        return provider.queryProvider(prompt, false);
    }

    /**
     * Generates a CSV format summary of the provided input text using the LLM provider.
     * The generated CSV will contain key information extracted from the input,
     * formatted in a human-readable manner with aligned columns and rows.
     *
     * @param body The input text to convert to CSV format
     * @return A generated CSV string containing key information from the input,
     *         or null if the provider fails or the engine is disabled
     */
    public String createCSVSummary(String body) {
        String prompt = CSV_GENERATOR_PROMPT + body;
        logger.info("Prompting LLM for CSV summary: \nSystem: {}\nBody: {}", CSV_GENERATOR_PROMPT, body);
        return provider.queryProvider(prompt, false);
    }
}
