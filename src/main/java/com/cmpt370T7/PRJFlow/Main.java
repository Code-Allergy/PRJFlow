package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.llm.*;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class is the entry point of the application, and seems to be
 * required for us to create fat-jar file. This should forever just call
 * the main application method after setting up static dependencies.
 */
public class Main {
    public static void main(final String[] args) {
        // load .env file in development
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();


        // TODO move this into application start, so we can have the user setup the provider if needed.
        // DI for LLM
        LlmProvider provider;
        if (dotenv.get("GROQAI_KEY") != null) {
            provider = CloudLlmProvider.createGroqProvider(dotenv.get("GROQAI_KEY"));
        } else if (dotenv.get("OPENAI_KEY") != null) {
            provider = CloudLlmProvider.createOpenAIProvider(dotenv.get("OPENAI_KEY"));
        } else {
            provider = new OllamaProvider();
            // check if the provider is working, if it is not, we will start with no provider
            if (!provider.isAvailable()) provider = null;
        }

        AiEngine.instantiate(provider);

        // Start the application
        PRJFlow.main(args);
    }
}
