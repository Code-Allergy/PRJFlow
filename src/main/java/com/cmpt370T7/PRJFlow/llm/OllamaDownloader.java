package com.cmpt370T7.PRJFlow.llm;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * This class is responsible for downloading the Ollama installer and running it.
 */
public class OllamaDownloader extends StackPane {
    private final String url = "https://ollama.com/download/OllamaSetup.exe";
    private final String installerPath = "./ollama.exe";
    private final Label label;
    private final ProgressBar progressBar;

    public OllamaDownloader() {
        progressBar = new ProgressBar(0);
        label = new Label("Downloading Ollama...");
        getChildren().add(progressBar);
        downloadFile(installerPath);
    }

    private void downloadFile(String destination) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                HttpURLConnection httpConnection = (HttpURLConnection) URI.create(url).toURL().openConnection();
                int totalSize = httpConnection.getContentLength();

                try (BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(destination)) {

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
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    private void runInstaller() {
        try {
            Process installerProcess = new ProcessBuilder(installerPath).start();
            installerProcess.waitFor();
            removeInstaller();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeInstaller() {
        try {
            new ProcessBuilder("cmd", "/c", "del", installerPath).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
