package com.cmpt370T7.PRJFlow;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePDFViewer extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(ImagePDFViewer.class);

    private final GUI gui;

    public ImagePDFViewer(File pdfFile, GUI gui) {
        this.gui = gui;
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        Button backButton = new Button("Back");

        backButton.setOnAction(e -> {
            gui.revertRightPane();
        });


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox pdfPagesBox = new VBox(10);
        scrollPane.setContent(pdfPagesBox);

        new Thread(() -> {
            try {
                PDDocument document = Loader.loadPDF(pdfFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                int pageCount = document.getNumberOfPages();
                List<ImageView> imageViews = new ArrayList<>();

                for (int page = 0; page < pageCount; ++page) {
                    Image pageImage = renderPageToImage(pdfRenderer, page);
                    ImageView imageView = new ImageView(pageImage);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(600); // Adjust as needed
                    imageViews.add(imageView);
                }

                document.close();

                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> pdfPagesBox.getChildren().addAll(imageViews));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        this.getChildren().addAll(backButton, scrollPane);
    }

    private Image renderPageToImage(PDFRenderer pdfRenderer, int pageIndex) throws IOException {
        // Render the page to a BufferedImage
        java.awt.image.BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 150);
        // Convert BufferedImage to JavaFX Image
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
