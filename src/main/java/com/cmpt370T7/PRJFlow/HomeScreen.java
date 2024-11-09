package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class HomeScreen extends BorderPane {
    private final Map<LocalDate, List<String>> remindersMap;
    private static final Logger logger = LoggerFactory.getLogger(HomeScreen.class);
    private final MainGUI mainGUI;
    private final List<Project> projects;
    private ListView<Project> projectsListView;
    private ListView<Project> recentProjectsListView;

    private Project selectedProject;

    public HomeScreen(MainGUI mainGUI, List<Project> projects) {
        this.mainGUI = mainGUI;
        this.projects = projects;
        this.remindersMap = AppDataManager.getInstance().getConfigManager().getReminderMap();

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
            selectedProject = projectsListView.getSelectionModel().getSelectedItem();
            logger.debug("Selection: {}", selectedProject);

            if (selectedProject != null) {
                if (event.getClickCount() == 2) { // Double-click
                    logger.info("Opening project: {}", selectedProject);
                    mainGUI.switchToProjectView(selectedProject);
                    selectedProject = null;
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
            selectedProject = recentProjectsListView.getSelectionModel().getSelectedItem();
            logger.debug("Recent Selection: {}", selectedProject);

            if (selectedProject != null) {
                if (event.getClickCount() == 2) { // Double-click
                    logger.info("Opening recent project: {}", selectedProject);
                    logger.info("Project path: {}", selectedProject.getDirectory());
                    mainGUI.switchToProjectView(selectedProject);
                    selectedProject = null;
                }
            }
        });

        Button newProjectButton = new Button("New Project");
        // Set action for new project button to create a new project
        newProjectButton.setOnAction(e -> createNewProject());

        Button deleteProjectButton = new Button("Delete Project");
        deleteProjectButton.setOnAction(e -> deleteProject());

        centerPane.getChildren().addAll(recentProjectsLabel, recentProjectsListView, newProjectButton, deleteProjectButton);
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
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Choose the project directory");

                File selectedFolder =  dc.showDialog(this.getScene().getWindow());
                if (selectedFolder != null) {
                    Project newProject = new Project(name.trim(), selectedFolder);
                    projects.addFirst(newProject); // Add to the top of the list
                    updateProjectsListView();
                    updateRecentProjectsListView();
                    AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                    try {
                        ProjectManager.saveProject(newProject, selectedFolder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mainGUI.switchToProjectView(newProject);
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Name");
                alert.setHeaderText(null);
                alert.setContentText("Project name cannot be empty.");
                alert.showAndWait();
            }
        });
    }

    private void deleteProject() {
        if (selectedProject != null) {
            Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete ", ButtonType.YES, ButtonType.NO);
            deleteConfirmation.setContentText("Are you sure you want to delete the project called: " + selectedProject.getName() + "?");
            deleteConfirmation.showAndWait();
            if (deleteConfirmation.getResult() == ButtonType.YES) {
                projects.remove(selectedProject);
                updateProjectsListView();
                updateRecentProjectsListView();
                AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
            }
        }

    }

    private void updateProjectsListView() {
        projectsListView.getItems().setAll(projects);
    }

    private void updateRecentProjectsListView() {
        // Assuming recent projects are the same as projects for now
        recentProjectsListView.getItems().setAll(projects);
    }
}
