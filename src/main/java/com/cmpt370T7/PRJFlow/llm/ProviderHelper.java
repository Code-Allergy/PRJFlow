package com.cmpt370T7.PRJFlow.llm;
import com.cmpt370T7.PRJFlow.AppDataManager;
import com.cmpt370T7.PRJFlow.ConfigManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * UI Helper class for setting the user up with the provider.
 * TODO feel free to refactor this class
 */
public class ProviderHelper {
    private static final Logger logger = LoggerFactory.getLogger(ProviderHelper.class);
    private final Stage primaryStage;

    /**
     * Constructor for ProviderHelper.
     *
     * @param primaryStage the primary stage of the application, used to spawn modal dialogs
     */
    public ProviderHelper(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Shows the provider selection dialog.
     *
     * @return a CompletableFuture that completes when the setup is done
     */
    public CompletableFuture<Void> showProviderSelectionDialog() {
        CompletableFuture<Void> setupComplete = new CompletableFuture<>();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("First Time Setup");

        Label titleLabel = new Label("Select a Provider.");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button localAiButton = new Button("Local AI (Slower, More Private)");
        localAiButton.setOnAction(e -> {
            // if ollama is already installed, we can skip downloading it again
            if (OllamaProvider.isOllamaInstalled()) {
                dialog.close();
                showSuccessDialog();
                AppDataManager.getInstance().getConfigManager()
                        .setLlmProviderConfig(ConfigManager.LlmProviderConfig.createOllamaProvider());
                setupComplete.complete(null);
                return;
            }
            dialog.close();
            openLocalAiSetup(dialog).thenRun(() -> {
                showSuccessDialog();
                AppDataManager.getInstance().getConfigManager()
                        .setLlmProviderConfig(ConfigManager.LlmProviderConfig.createOllamaProvider());
                setupComplete.complete(null);
            });
        });

        Button webUiButton = new Button("Web UI (Faster, Cloud-Based)");
        webUiButton.setOnAction(e -> {
            dialog.close();
            openWebUiSetup().thenRun(() -> {
                logger.debug("Web UI setup complete");
                showSuccessDialog();
                setupComplete.complete(null);
            });
        });

        Button geminiButton = new Button("Google Gemini (Cloud-Based)");
        geminiButton.setOnAction(e -> {
            dialog.close();
            CompletableFuture<Void> geminiSetupFuture = openGeminiSetup(dialog);
            geminiSetupFuture.thenRun(() -> {
                logger.debug("Gemini setup process finished successfully.");
                showSuccessDialog();
                setupComplete.complete(null); // Complete the main setup future
            }).exceptionally(ex -> {
                logger.warn("Gemini setup was cancelled or failed: " + (ex != null ? ex.getMessage() : "Unknown reason"));
                // Optionally, re-show the provider selection dialog or handle other cleanup
                return null;
            });
        });

        VBox layout = new VBox(20);
        layout.getChildren().addAll(titleLabel, localAiButton, webUiButton, geminiButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.show();

        return setupComplete;
    }

    /**
     * Opens the local AI setup dialog.
     *
     * @return a CompletableFuture that completes when the setup is done
     */
    private CompletableFuture<Void> openLocalAiSetup(Stage dialog) {
        CompletableFuture<Void> complete = new CompletableFuture<>();
        Stage localAiStage = new Stage();
        localAiStage.initModality(Modality.APPLICATION_MODAL);
        localAiStage.setTitle("Local AI Setup");

        OllamaDownloader ollamaDownloader = new OllamaDownloader(complete, dialog);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(
                ollamaDownloader
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        localAiStage.setScene(scene);
        localAiStage.show();

        return complete;
    }

    /**
     * Opens the web UI setup dialog.
     *
     * @return a CompletableFuture that completes when the setup is done
     */
    private CompletableFuture<Void> openWebUiSetup() {
        CompletableFuture<Void> complete = new CompletableFuture<>();
        Stage webUiStage = new Stage();
        webUiStage.initModality(Modality.APPLICATION_MODAL);
        webUiStage.setTitle("Groq Cloud Setup");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Step 1: Create Account
        Tab accountTab = new Tab("1. Create Account");
        VBox accountContent = new VBox(10);
        accountContent.setPadding(new Insets(20));
        accountContent.setAlignment(Pos.CENTER);

        Label accountLabel = new Label("First, create a Groq Cloud account:");
        TextField backupUrlField = new TextField("https://console.groq.com/keys");
        backupUrlField.setEditable(false);
        backupUrlField.setFocusTraversable(false);
        backupUrlField.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0; -fx-alignment: center;");        Button openGroqButton = new Button("Open Groq Cloud Website");
        openGroqButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://console.groq.com/keys"));
            } catch (Exception ex) {
                logger.error("Failed to open Groq Cloud website", ex);
            }
        });

        accountContent.getChildren().addAll(
                accountLabel,
                openGroqButton,
                new Label("Don't worry, it's free!"),
                backupUrlField,
                new Label("Once you've created your account, click 'Next'")
        );
        accountTab.setContent(accountContent);

        // Step 2: Get API Key
        Tab apiKeyTab = new Tab("2. Get API Key");
        VBox apiKeyContent = new VBox(10);
        apiKeyContent.setPadding(new Insets(20));
        apiKeyContent.setAlignment(Pos.CENTER);

        VBox instructionsBox = new VBox(5);
        instructionsBox.getChildren().addAll(
                new Label("To get your API key:"),
                new Label("1. Click on your profile in the top right"),
                new Label("2. Select 'API Keys'"),
                new Label("3. Click 'Create API Key'"),
                new Label("4. Give it a name and create"),
                new Label("5. Copy the API key (you won't see it again!)")
        );

        apiKeyContent.getChildren().addAll(
                instructionsBox
        );
        apiKeyTab.setContent(apiKeyContent);

        // Step 3: Enter API Key
        Tab enterKeyTab = new Tab("3. Enter API Key");
        VBox enterKeyContent = new VBox(10);
        enterKeyContent.setPadding(new Insets(20));
        enterKeyContent.setAlignment(Pos.CENTER);

        Label enterKeyLabel = new Label("Enter your Groq API Key:");
        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setMaxWidth(300);

        Button saveButton = new Button("Save API Key");
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        saveButton.setOnAction(e -> {
            String apiKey = apiKeyField.getText().trim();
            if (apiKey.isEmpty()) {
                statusLabel.setText("Please enter an API key");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            try {
                // Save the API key to the config
                AppDataManager.getInstance().getConfigManager()
                        .setLlmProviderConfig(ConfigManager.LlmProviderConfig.createGroqProvider(apiKey));

                statusLabel.setText("API key saved successfully!");
                statusLabel.setTextFill(Color.GREEN);
                // we could check if the key is valid here, but we'll just assume it is for now

                webUiStage.close();
                complete.complete(null);

            } catch (Exception ex) {
                statusLabel.setText("Error saving API key: " + ex.getMessage());
                statusLabel.setTextFill(Color.RED);
                logger.error("Failed to save Groq API key", ex);
            }
        });

        enterKeyContent.getChildren().addAll(
                enterKeyLabel,
                apiKeyField,
                saveButton,
                statusLabel
        );
        enterKeyTab.setContent(enterKeyContent);

        // Add all tabs
        tabPane.getTabs().addAll(accountTab, apiKeyTab, enterKeyTab);

        // Create Next/Back buttons
        Button backButton = new Button("← Back");
        Button nextButton = new Button("Next →");

        backButton.setOnAction(e -> {
            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex > 0) {
                tabPane.getSelectionModel().select(currentIndex - 1);
            }
        });

        nextButton.setOnAction(e -> {
            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex < tabPane.getTabs().size() - 1) {
                tabPane.getSelectionModel().select(currentIndex + 1);
            }
        });

        // Navigation buttons layout
        HBox navigationButtons = new HBox(10);
        navigationButtons.setAlignment(Pos.CENTER);
        navigationButtons.getChildren().addAll(backButton, nextButton);

        // Update button states based on selected tab
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            backButton.setDisable(newVal.intValue() == 0);
            nextButton.setDisable(newVal.intValue() == tabPane.getTabs().size() - 1);
        });

        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(tabPane, navigationButtons);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 400, 500);
        webUiStage.setScene(scene);
        webUiStage.show();

        return complete;
    }

    /**
     * Shows a success dialog indicating the setup is complete.
     */
    private void showSuccessDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Setup Complete");

        Label titleLabel = new Label("Setup Complete!");
        Label messageLabel = new Label("You are now ready to generate text.");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialog.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(titleLabel, messageLabel, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private CompletableFuture<Void> openGeminiSetup(Stage parentDialog) {
        CompletableFuture<Void> setupSuccessful = new CompletableFuture<>();

        Stage geminiStage = new Stage();
        geminiStage.initModality(Modality.APPLICATION_MODAL);
        geminiStage.initOwner(parentDialog); // Use parentDialog here if needed, or primaryStage
        geminiStage.setTitle("Google Gemini Setup");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Get API Key
        Tab getKeyTab = new Tab("1. Get API Key");
        VBox getKeyContent = new VBox(10);
        getKeyContent.setPadding(new Insets(20));
        getKeyContent.setAlignment(Pos.CENTER_LEFT); // Align text to left for readability

        Label infoLabel = new Label("First, get your Google Gemini API key from Google AI Studio.");
        TextField urlField = new TextField("https://aistudio.google.com/app/apikey");
        urlField.setEditable(false);
        urlField.setFocusTraversable(false);
        urlField.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 3 0 3 0;"); // Adjusted padding

        Button openStudioButton = new Button("Open Google AI Studio");
        openStudioButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://aistudio.google.com/app/apikey"));
            } catch (Exception ex) {
                logger.error("Failed to open Google AI Studio website", ex);
                // Optionally show an error to the user
            }
        });

        VBox instructionsBox = new VBox(5);
        instructionsBox.getChildren().addAll(
                new Label("Instructions:"),
                new Label("1. Click 'Open Google AI Studio' above."),
                new Label("2. Sign in if prompted."),
                new Label("3. Click 'Create API key'. You might need to create a new project."),
                new Label("4. Copy the generated API key."),
                new Label("5. Click 'Next' and paste the key in the next tab.")
        );

        getKeyContent.getChildren().addAll(infoLabel, urlField, openStudioButton, new Separator(), instructionsBox);
        getKeyTab.setContent(getKeyContent);

        // Tab 2: Enter API Key
        Tab enterKeyTab = new Tab("2. Enter API Key");
        VBox enterKeyContent = new VBox(10);
        enterKeyContent.setPadding(new Insets(20));
        enterKeyContent.setAlignment(Pos.CENTER);

        Label enterKeyLabel = new Label("Enter your Gemini API Key:");
        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setMaxWidth(300);
        apiKeyField.setPromptText("Paste your API key here");

        Button saveApiKeyButton = new Button("Save API Key");
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        saveApiKeyButton.setOnAction(e -> {
            String apiKey = apiKeyField.getText().trim();
            if (apiKey.isEmpty()) {
                statusLabel.setText("API key cannot be empty.");
                statusLabel.setTextFill(Color.RED);
                return;
            }
            try {
                AppDataManager.getInstance().getConfigManager()
                        .setLlmProviderConfig(ConfigManager.LlmProviderConfig.createGeminiProvider(apiKey));
                statusLabel.setText("API key saved successfully!");
                statusLabel.setTextFill(Color.GREEN);
                logger.info("Google Gemini API key saved.");

                // Close the dialog and complete the future
                geminiStage.close();
                setupSuccessful.complete(null);

            } catch (Exception ex) {
                statusLabel.setText("Error saving API key: " + ex.getMessage());
                statusLabel.setTextFill(Color.RED);
                logger.error("Failed to save Gemini API key", ex);
            }
        });

        enterKeyContent.getChildren().addAll(enterKeyLabel, apiKeyField, saveApiKeyButton, statusLabel);
        enterKeyTab.setContent(enterKeyContent);

        tabPane.getTabs().addAll(getKeyTab, enterKeyTab);

        Button backButton = new Button("← Back");
        Button nextButton = new Button("Next →");

        backButton.setOnAction(e -> {
            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex > 0) {
                tabPane.getSelectionModel().select(currentIndex - 1);
            }
        });
        nextButton.setOnAction(e -> {
            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex < tabPane.getTabs().size() - 1) {
                tabPane.getSelectionModel().select(currentIndex + 1);
            }
        });

        HBox navigationButtons = new HBox(10, backButton, nextButton);
        navigationButtons.setAlignment(Pos.CENTER);

        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            backButton.setDisable(newVal.intValue() == 0);
            nextButton.setDisable(newVal.intValue() == tabPane.getTabs().size() - 1);
            saveApiKeyButton.setVisible(newVal.intValue() == tabPane.getTabs().size() - 1); // Show save only on last tab
        });
        // Initial state for buttons
        backButton.setDisable(true);
        saveApiKeyButton.setVisible(tabPane.getSelectionModel().getSelectedIndex() == tabPane.getTabs().size() -1);


        VBox mainLayout = new VBox(10, tabPane, navigationButtons);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 450, 400); // Adjusted size
        geminiStage.setScene(scene);

        geminiStage.setOnCloseRequest(event -> {
            if (!setupSuccessful.isDone()) {
                logger.info("Gemini setup dialog closed by user without saving.");
                // Optionally complete exceptionally to signal cancellation
                setupSuccessful.completeExceptionally(new RuntimeException("Gemini setup cancelled by user."));
            }
        });

        geminiStage.showAndWait(); // Use showAndWait to block until this dialog is handled

        return setupSuccessful;
    }


    // TODO failure dialog, or show AlertHelper.showError
}
