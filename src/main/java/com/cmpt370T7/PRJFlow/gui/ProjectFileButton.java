package com.cmpt370T7.PRJFlow.gui;

import com.cmpt370T7.PRJFlow.Project;
import com.cmpt370T7.PRJFlow.WebPDFViewer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.function.Consumer;

public class ProjectFileButton extends Button {
    private static final Logger logger = LoggerFactory.getLogger(ProjectFileButton.class);

    private static final String BUTTON_FONT = "Courier";
    private static final double BUTTON_FONT_SIZE = 11;
    private static final int BUTTON_MAX_WIDTH = 80;

    private static final int FILE_ICON_SIZE = 30;

    private static final int MAX_FILE_NAME_LENGTH = 26;

    private final FontIcon fileIcon;

    public ProjectFileButton(File file) {
        super();
        this.setAlignment(Pos.CENTER);
        this.fileIcon = new FontIcon();

        this.setFont(Font.font(BUTTON_FONT,  BUTTON_FONT_SIZE));
        setIcon(file);
        this.fileIcon.setIconSize(FILE_ICON_SIZE);

        this.setText(truncateFileName(file.getName()));
        this.setTextAlignment(TextAlignment.CENTER);
        this.wrapTextProperty().setValue(true);

        this.setMaxWidth(BUTTON_MAX_WIDTH);

        this.setContentDisplay(ContentDisplay.TOP);
        this.setGraphic(fileIcon);
        this.setId(file.getName());
    }

    private void setIcon(File file) {
        String mimeType = null;

        try {
            // Try to probe the actual content type first
            mimeType = Files.probeContentType(file.toPath());

            // If that fails, fall back to URLConnection's guess
            if (mimeType == null) {
                mimeType = URLConnection.guessContentTypeFromName(file.getName());
            }

            // If both methods fail, fall back to extension-based approach
            if (mimeType == null) {
                String fileName = file.getName();
                int lastDotIndex = fileName.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
                    mimeType = switch(extension) {
                        case "pdf" -> "application/pdf";
                        case "doc", "docx" -> "application/msword";
                        case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
                        case "xls", "xlsx" -> "application/vnd.ms-excel";
                        case "txt" -> "text/plain";
                        default -> "application/octet-stream";
                    };
                }
            }

            // Set the icon based on the MIME type
            if (mimeType != null) {
                switch (mimeType) {
                    case "application/pdf":
                        fileIcon.setIconLiteral("mdi-file-pdf");
                        break;
                    case "application/msword":
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                        fileIcon.setIconLiteral("mdi-file-word");
                        break;
                    case "application/vnd.ms-powerpoint":
                    case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                        fileIcon.setIconLiteral("mdi-file-powerpoint");
                        break;
                    case "application/vnd.ms-excel":
                    case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                        fileIcon.setIconLiteral("mdi-file-excel");
                        break;
                    case "text/plain":
                        fileIcon.setIconLiteral("mdi-file-document");
                        break;
                    default:
                        fileIcon.setIconLiteral("mdi-file");
                        break;
                }
            } else {
                fileIcon.setIconLiteral("mdi-file");
            }
        } catch (IOException e) {
            logger.warn("Failed to probe content type for file: {}", file.getName(), e);
            fileIcon.setIconLiteral("mdi-file");
        }
    }

    private String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension.toLowerCase();
    }

    private String truncateFileName(String fileName) {
        String extension = getExtension(fileName);
        if (fileName.length() > MAX_FILE_NAME_LENGTH) {
            return fileName.substring(0, (MAX_FILE_NAME_LENGTH - extension.length() - 3)) + "..." + extension;
        } else {
            return fileName;
        }
    }
}
