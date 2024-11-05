package com.cmpt370T7.PRJFlow;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PRJFlow extends Application {
    private static final Logger logger = LoggerFactory.getLogger(PRJFlow.class);
    
    @Override
    public void start(Stage stage) {
        try {
            AppDataManager.instantiate();
        } catch (IOException e) {
            logger.error("Failed to start appdata manager", e);
            // TODO Display error to user.
        }
        MainGUI root = new MainGUI();
        Scene scene = new Scene(root);
        stage.setTitle("PRJFlow");
        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();

        stage.setOnCloseRequest(e -> {
            logger.info("Closing application...");
            try {
                AppDataManager.getInstance().getConfigManager().saveConfig();
            } catch (IOException e1) {
                logger.error("Failed to save configuration file when exiting", e1);
                
                // consume close request
                e.consume();

                // TODO display error to user
            }
        });
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}
