package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;

public class HomeScreen extends BorderPane {

    public HomeScreen() {
        // Set padding for the entire BorderPane
        this.setPadding(new Insets(10));

        // Left pane: Project list
        VBox leftPane = createLeftPane();

        // Right pane: Calendar and reminders
        VBox rightPane = createRightPane();

        // Center pane: Recent projects and new project button
        VBox centerPane = createCenterPane();

        // Set the panes in the BorderPane
        this.setLeft(leftPane);
        this.setRight(rightPane);
        this.setCenter(centerPane);
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-background-color: #f0f0f0;");

        Label projectsLabel = new Label("Projects");
        ListView<String> projectsList = new ListView<>();
        // Placeholder items
        projectsList.getItems().addAll("Project 1", "Project 2", "Project 3");

        leftPane.getChildren().addAll(projectsLabel, projectsList);
        return leftPane;
    }

    private VBox createRightPane() {
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #f0f0f0;");

        Label calendarLabel = new Label("Calendar");
        // Placeholder for calendar
        Text calendarPlaceholder = new Text("Calendar View Here");

        Label remindersLabel = new Label("Reminders");
        // Placeholder for reminders
        Text remindersPlaceholder = new Text("Reminders List Here");

        rightPane.getChildren().addAll(calendarLabel, calendarPlaceholder, remindersLabel, remindersPlaceholder);
        return rightPane;
    }

    private VBox createCenterPane() {
        VBox centerPane = new VBox(20);
        centerPane.setPadding(new Insets(10));

        Label recentProjectsLabel = new Label("Recent Projects");
        // Placeholder for recent projects
        ListView<String> recentProjectsList = new ListView<>();
        recentProjectsList.getItems().addAll("Recent Project 1", "Recent Project 2");

        Button newProjectButton = new Button("New Project");
        // Set action for new project button
        newProjectButton.setOnAction(e -> {
            openFileChooser();
        });

        centerPane.getChildren().addAll(recentProjectsLabel, recentProjectsList, newProjectButton);
        return centerPane;
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        // Open the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            // Open the PDF viewer with the selected file
            PDFViewer pdfViewer = new PDFViewer(selectedFile);
            // Replace the current view with the PDF viewer
            this.getScene().setRoot(pdfViewer);
        }
    }
}
