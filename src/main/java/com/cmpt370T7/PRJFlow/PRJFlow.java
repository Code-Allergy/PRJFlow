package com.cmpt370T7.PRJFlow;

import java.io.IOException;

import com.cmpt370T7.PRJFlow.llm.OllamaDownloader;
import com.cmpt370T7.PRJFlow.llm.ProviderHelper;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
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

        try {
            AppDataManager.instantiate();
            logger.debug("AppDataManager instantiated");
        } catch (IOException e) {
            logger.error("Failed to start appdata manager", e);
            // TODO Display error to user.
        }

        GUI root = new GUI();
        Scene scene = new Scene(root);
        stage.setTitle("PRJFlow");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();

        ProviderHelper helper = new ProviderHelper(stage);
        helper.showProviderSelectionDialog();

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

        root.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}
