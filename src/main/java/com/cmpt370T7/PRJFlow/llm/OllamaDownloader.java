package com.cmpt370T7.PRJFlow.llm;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for downloading the Ollama installer and running it.
 * It extends VBox to provide a UI component that shows the download progress.
 */
public class OllamaDownloader extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(OllamaDownloader.class);

    private final String url = "https://ollama.com/download/OllamaSetup.exe";
    private final String installerPath = "./OllamaSetup.exe";
    private final Label progressLabel = new Label();
    private final ProgressBar progressBar;
    private final Stage popUpStage;

    CompletableFuture<Void> completed;

    /**
     * Constructor for OllamaDownloader.
     * Initializes the progress bar and starts the download process.
     */
    public OllamaDownloader(CompletableFuture<Void> completed, Stage popUpStage) {
        this.progressBar = new ProgressBar(0);
        this.completed = completed;
        this.popUpStage = popUpStage;
        getChildren().addAll(progressLabel, progressBar);
        downloadFile();
    }


    /**
     * Downloads the installer file from the specified URL.
     * Updates the progress bar and label during the download.
     * Runs the installer and downloads models after the download is complete.
     */
    private void downloadFile() {
        Platform.runLater(() -> this.progressLabel.setText("Downloading installer..."));
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                HttpURLConnection httpConnection = (HttpURLConnection) URI.create(url).toURL().openConnection();
                int totalSize = httpConnection.getContentLength();

                try (BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(installerPath)) {

                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    int downloadedSize = 0;

                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        downloadedSize += bytesRead;
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        updateProgress(downloadedSize, totalSize);
                    }
                }
                runInstaller();
                downloadModel();
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        task.setOnSucceeded(event -> {
            logger.info("Download complete");
            this.progressLabel.setText("Setup complete! You can close this window now.");
            this.getChildren().remove(progressBar);
            this.completed.complete(null);
            popUpStage.close(); // For some reason, this call isn't working. TODO: Fix this.
        });
        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            progressLabel.textProperty().unbind();
            progressLabel.setText("Download failed: " + task.getException().getMessage());
        });
    }

    /**
     * Downloads the model required for the Ollama provider.
     * TODO Updates the progress label during the download.
     */
    private void downloadModel() {
        Platform.runLater(() -> this.progressLabel.setText("Downloading model..."));
        logger.debug("Downloading model...");
        // could reset the progress bar here, but don't know how to get the status of our pull..
        OllamaProvider.pullModel(OllamaProvider.getOllamaDefaultModel());
    }

    /**
     * Runs the downloaded installer.
     * Removes the installer file after installation.
     */
    private void runInstaller() {
        Platform.runLater(() -> this.progressLabel.setText("Running installer..."));
        try {
            // Start the installer with elevated privileges if needed
            ProcessBuilder processBuilder = new ProcessBuilder(installerPath);
            processBuilder.redirectErrorStream(true); // Merge stderr into stdout
            Process installerProcess = processBuilder.start();

            // Read the output stream to prevent potential deadlock
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(installerProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Installer output: {}", line);
                }
            }

            // Wait for the process with a timeout
            if (!installerProcess.waitFor(5, TimeUnit.MINUTES)) {
                logger.error("Installer process timed out");
                installerProcess.destroyForcibly();
                throw new RuntimeException("Installer timed out");
            }

            // Check exit code
            int exitCode = installerProcess.exitValue();
            if (exitCode != 0) {
                logger.error("Installer failed with exit code: {}", exitCode);
                throw new RuntimeException("Installer failed with exit code: " + exitCode);
            }

            // Add a small delay to ensure file handles are released
            Thread.sleep(2000);

            removeInstaller();
        } catch (IOException e) {
            logger.error("IO error during installation", e);
            throw new RuntimeException("Installation failed", e);
        } catch (InterruptedException e) {
            logger.error("Installation was interrupted", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the installer file after installation.
     * Updates the progress label during the removal process.
     * TODO appears to be non-functional, and fail silently. Look into this if time permits, if we can't get this working maybe we should store downloaded binary in ~/AppData/Local/Temp
     *
     */
    private void removeInstaller() {
        logger.debug("Removing installer...");
        Platform.runLater(() -> this.progressLabel.setText("Removing installer..."));
        try {
            new ProcessBuilder("cmd", "/c", "del", installerPath).start();
        } catch (IOException e) {
            logger.debug("Failed to remove installer: {}", e.getMessage());
        }
    }
}
