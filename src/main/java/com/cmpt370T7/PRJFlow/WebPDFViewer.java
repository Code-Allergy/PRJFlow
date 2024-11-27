package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.gui.GUI;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;

/**
 * WebPDFViewer is a JavaFX component that displays a PDF file within a WebView,
 * allowing the user to view PDF content in a browser-like interface.
 * This class integrates with a JS PDF viewer (pdfjs) and displays a back button
 * for navigation within the application.
 */
public class WebPDFViewer extends VBox {
    /** Logger for reporting errors and debug information. */
    private final static Logger logger = LoggerFactory.getLogger(WebPDFViewer.class);

    /**
     * Constructs a WebPDFViewer component that displays the specified PDF file.
     *
     * @param pdfFile The PDF file to be viewed in the WebPDFViewer.
     * @param gui The GUI object that manages the application's views.
     */
    public WebPDFViewer(File pdfFile, GUI gui) {
        WebView webView = new WebView();
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            gui.revertRightPane();
        });
        WebEngine engine = webView.getEngine();
        try {
            String url = Objects.requireNonNull(getClass().getResource("/web/viewer.html")).toURI().toString();
            engine.setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("/web/viewer.css")).toURI().toString());
            engine.setJavaScriptEnabled(true);
            engine.load(url);
            engine.setOnError(event -> logger.error("WebEngine error: {}", event.getMessage()));
            engine.setOnAlert(event -> logger.warn("WebEngine alert: {}", event.getData()));
        } catch (URISyntaxException e) { // This should never happen?
            logger.error("Unable to load web viewer", e);
        }

        engine.getLoadWorker()
                .stateProperty()
                .addListener((obs, oldV, newV) -> {
                    if (Worker.State.SUCCEEDED == newV) {
                        try {
                            InputStream is = new FileInputStream(pdfFile);
                            byte[] bytes = IOUtils.toByteArray(is);
                            String base64 = Base64.getEncoder().encodeToString(bytes);

                            // This MUST be run on FXApplicationThread
                            engine.executeScript("openFileFromBase64('" + base64 + "')");

                        } catch (Exception ex) {
                            logger.error("Unable to load file in web viewer: {}", pdfFile.getAbsolutePath(), ex);
                            AlertHelper.showError("Error", "Failed to load file in web viewer");
                            // TODO: throw exception up the stack, for now, just bail out
                            gui.revertRightPane();
                        }
                    }
                });

        this.getChildren().addAll(backButton, webView);
    }
}
