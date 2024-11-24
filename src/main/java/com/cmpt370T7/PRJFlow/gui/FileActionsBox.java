package com.cmpt370T7.PRJFlow.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class FileActionsBox extends HBox {
    private static final double SPACING = 10;
    private static final double PADDING = 5;

    private final FileActionButton addFileButton;
    private final FileActionButton removeFileButton;
    private final FileActionButton exportButton;
    private final FileActionButton summarizeButton;
    private final FileActionButton addDeadlineButton;

    // Properties to control button states
    private final BooleanProperty hasSelectedProject = new SimpleBooleanProperty(false);
    private final BooleanProperty hasSelectedFile = new SimpleBooleanProperty(false);


    public FileActionsBox() {
        setupLayout();

        addFileButton = createActionButton("Add File", "mdi-plus-box", "Add new files to the project");
        removeFileButton = createActionButton("Remove File", "mdi-delete", "Remove selected files");
        exportButton = createActionButton("Export", "mdi-export", "Export selected files");
        summarizeButton = createActionButton("Summarize", "mdi-creation", "Generate summary for selected files");
        addDeadlineButton = createActionButton("Add Deadline", "mdi-calendar-plus", "Add a deadline to the project");

        setupButtonBinding();
        this.getChildren().addAll(addFileButton, removeFileButton, exportButton, summarizeButton);
    }

    private void setupLayout() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(SPACING);
        setPadding(new Insets(PADDING));
    }

    private void setupButtonBinding() {
        addFileButton.disableProperty().bind(hasSelectedProject.not());
        removeFileButton.disableProperty().bind(hasSelectedFile.not());
        exportButton.disableProperty().bind(hasSelectedFile.not());
        summarizeButton.disableProperty().bind(hasSelectedFile.not());
    }

    public BooleanProperty hasSelectedFilesProperty() {
        return hasSelectedFile;
    }

    public BooleanProperty hasSelectedProjectProperty() {
        return hasSelectedProject;
    }

    public void setAddFileButtonAction(Runnable action) {
        addFileButton.setOnAction(e -> action.run());
    }

    public void setRemoveFileButtonAction(Runnable action) {
        removeFileButton.setOnAction(e -> action.run());
    }

    public void setExportButtonAction(Runnable action) {
        exportButton.setOnAction(e -> action.run());
    }

    public void setSummarizeButtonAction(Runnable action) {
        summarizeButton.setOnAction(e -> action.run());
    }

    public void setAddDeadlineButton(Runnable action) {
        addDeadlineButton.setOnAction(e -> action.run());
    }

    private static class FileActionButton extends Button {
        FileActionButton(String text, String iconCode) {
            super(text);
            FontIcon icon = new FontIcon(iconCode);
            setGraphic(icon);
            getStyleClass().add("action-button");
            setFocusTraversable(false);
        }
    }


    private FileActionButton createActionButton(String text, String iconCode, String tooltipText) {
        FileActionButton button = new FileActionButton(text, iconCode);
        button.setTooltip(new Tooltip(tooltipText));
        button.getStyleClass().add("accent-button");
        return button;
    }

}
