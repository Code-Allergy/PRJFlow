package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.*;

public class HomeScreen extends BorderPane {

    private Map<LocalDate, List<String>> remindersMap = new HashMap<>();
    private final MainGUI mainGUI;
    private List<Project> projects;
    private ListView<Project> projectsListView;
    private ListView<Project> recentProjectsListView;

    public HomeScreen(MainGUI mainGUI, List<Project> projects) {
        this.mainGUI = mainGUI;
        this.projects = projects;

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
        projectsListView = new ListView<>();
        updateProjectsListView();

        // Handle project selection
        projectsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Project selectedProject = projectsListView.getSelectionModel().getSelectedItem();
                if (selectedProject != null) {
                    mainGUI.switchToProjectView(selectedProject);
                }
            }
        });

        leftPane.getChildren().addAll(projectsLabel, projectsListView);
        return leftPane;
    }

    private VBox createRightPane() {
        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #f0f0f0;");

        // Create the custom calendar and pass the remindersMap
        CustomCalendar customCalendar = new CustomCalendar(remindersMap);

        rightPane.getChildren().add(customCalendar);
        return rightPane;
    }

    private VBox createCenterPane() {
        VBox centerPane = new VBox(20);
        centerPane.setPadding(new Insets(10));

        Label recentProjectsLabel = new Label("Recent Projects");
        recentProjectsListView = new ListView<>();
        updateRecentProjectsListView();

        // Handle recent project selection
        recentProjectsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Project selectedProject = recentProjectsListView.getSelectionModel().getSelectedItem();
                if (selectedProject != null) {
                    mainGUI.switchToProjectView(selectedProject);
                }
            }
        });

        Button newProjectButton = new Button("New Project");
        // Set action for new project button to create a new project
        newProjectButton.setOnAction(e -> createNewProject());

        centerPane.getChildren().addAll(recentProjectsLabel, recentProjectsListView, newProjectButton);
        return centerPane;
    }

    private void createNewProject() {
        // Prompt the user for a project name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Project Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Project newProject = new Project(name.trim());
                projects.add(0, newProject); // Add to the top of the list
                updateProjectsListView();
                updateRecentProjectsListView();
                mainGUI.switchToProjectView(newProject);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Name");
                alert.setHeaderText(null);
                alert.setContentText("Project name cannot be empty.");
                alert.showAndWait();
            }
        });
    }

    private void updateProjectsListView() {
        projectsListView.getItems().setAll(projects);
    }

    private void updateRecentProjectsListView() {
        // Assuming recent projects are the same as projects for now
        recentProjectsListView.getItems().setAll(projects);
    }
}
