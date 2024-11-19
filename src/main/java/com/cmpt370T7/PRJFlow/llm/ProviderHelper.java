package com.cmpt370T7.PRJFlow.llm;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import javafx.stage.Stage;

import java.awt.*;

// UI Helper class for setting the user up with the provider
// TODO feel free to refactor this class.
public class ProviderHelper {
    private Stage primaryStage;

    public ProviderHelper(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showProviderSelectionDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("First Time Setup");

        Label titleLabel = new Label("Select a Provider.");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button localAiButton = new Button("Local AI (Slower, More Private)");
        localAiButton.setOnAction(e -> {
            if (OllamaProvider.isOllamaInstalled()) {
                dialog.close();
                showSuccessDialog();
                // write to config here.
                return;
            }
            dialog.close();
            openLocalAiSetup();
        });

        Button webUiButton = new Button("Web UI (Faster, Cloud-Based)");
        webUiButton.setOnAction(e -> {
            dialog.close();
            openWebUiSetup();
        });

        VBox layout = new VBox(20);
        layout.getChildren().addAll(titleLabel, localAiButton, webUiButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void openLocalAiSetup() {
        Stage localAiStage = new Stage();
        localAiStage.initModality(Modality.APPLICATION_MODAL);
        localAiStage.setTitle("Local AI Setup");

        OllamaDownloader ollamaDownloader = new OllamaDownloader();

        VBox layout = new VBox(20);
        layout.getChildren().addAll(
                new Label("Downloading and Installing Ollama"),
                ollamaDownloader
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        localAiStage.setScene(scene);
        localAiStage.show();
    }

    // TODO
    private void openWebUiSetup() {
    }

    private void showSuccessDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Setup Complete");

        Label titleLabel = new Label("Setup Complete! You are now ready to generate text.");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialog.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(titleLabel, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
