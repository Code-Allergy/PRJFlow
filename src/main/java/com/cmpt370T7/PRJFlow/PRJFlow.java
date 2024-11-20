package com.cmpt370T7.PRJFlow;

import java.io.IOException;

import com.cmpt370T7.PRJFlow.llm.*;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PRJFlow extends Application {
    private static final Logger logger = LoggerFactory.getLogger(PRJFlow.class);

    @Override
    public void start(Stage stage) {
        logger.info("Starting application...");

        initialize_appdata();
        start_mainui(stage);
        initialize_llms(stage);
        setup_on_exit(stage);
    }

    private void start_mainui(Stage stage) {
        GUI root = new GUI();
        Scene scene = new Scene(root);
        stage.setTitle("PRJFlow");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();
        root.requestFocus();
    }

    private void initialize_appdata() {
        try {
            AppDataManager.instantiate();
            logger.debug("AppDataManager instantiated");
        } catch (IOException e) {
            logger.error("Failed to start appdata manager", e);
            AlertHelper.showError("Error", "Failed to start AppDataManager, please check logs for more information.");
        }
    }

    private void initialize_llms(Stage stage) {
        // Check if the user has set up the provider
        ConfigManager.LlmProviderConfig providerConfig = AppDataManager.getInstance().getConfigManager().getLlmProviderConfig();
        if (providerConfig == null) {
            logger.info("No LLM provider set up, prompting user to set up provider...");
            Platform.runLater(() -> {
                ProviderHelper providerHelper = new ProviderHelper(stage);
                providerHelper.showProviderSelectionDialog().thenRun(() -> {
                    // this is sloppy. but it works for now.
                    logger.info("First time setup complete, starting LLM provider...");
                    ConfigManager.LlmProviderConfig afterSetupProviderConfig = AppDataManager.getInstance().getConfigManager().getLlmProviderConfig();
                    instantiateAiEngine(afterSetupProviderConfig);
                });
            });
        } else {
            instantiateAiEngine(providerConfig);
        }
    }

    private void instantiateAiEngine(ConfigManager.LlmProviderConfig providerConfig) {
        logger.info("Starting connection with LLM provider: {}", providerConfig.provider());
        switch (providerConfig.provider()) {
            case "GroqCloud" -> AiEngine.instantiate(CloudLlmProvider.createGroqProvider(providerConfig.key()));
            case "OpenAI" -> AiEngine.instantiate(CloudLlmProvider.createOpenAIProvider(providerConfig.key()));
            case "Ollama" -> AiEngine.instantiate(new OllamaProvider());
            default -> {
                logger.error("Invalid provider name in config file: {}", providerConfig.provider());
                AlertHelper.showError("Error", "Invalid provider name in config file, please check logs for more information.");
            }
        }
    }

    private void setup_on_exit(Stage stage) {
        stage.setOnCloseRequest(e -> {
            logger.info("Closing application...");
            try {
                AppDataManager.getInstance().getConfigManager().saveConfig();
            } catch (IOException e1) {
                logger.error("Failed to save configuration file when exiting", e1);
                AlertHelper.showError("Error", "Failed to save configuration file when exiting");

                // consume close request, keeping the window open
                e.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
