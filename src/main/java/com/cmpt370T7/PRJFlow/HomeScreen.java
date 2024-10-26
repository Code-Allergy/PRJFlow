package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.*;

public class HomeScreen extends BorderPane {

    private Map<LocalDate, List<String>> remindersMap = new HashMap<>();
    private final MainGUI mainGUI;

    public HomeScreen(MainGUI mainGUI) {
        this.mainGUI = mainGUI;

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
        // Placeholder for recent projects
        ListView<String> recentProjectsList = new ListView<>();
        recentProjectsList.getItems().addAll("Recent Project 1", "Recent Project 2");

        Button newProjectButton = new Button("New Project");
        // Set action for new project button to open ProjectView
        newProjectButton.setOnAction(e -> mainGUI.switchToProjectView());

        centerPane.getChildren().addAll(recentProjectsLabel, recentProjectsList, newProjectButton);
        return centerPane;
    }
}
