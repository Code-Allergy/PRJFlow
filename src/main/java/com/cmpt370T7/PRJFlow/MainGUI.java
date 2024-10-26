package com.cmpt370T7.PRJFlow;

import javafx.scene.layout.StackPane;
import java.util.ArrayList;
import java.util.List;

public class MainGUI extends StackPane {

    private List<Project> projects;

    public MainGUI() {
        this.setPrefSize(800, 600);

        // Initialize the list of projects
        projects = new ArrayList<>();

        // Create HomeScreen and pass the projects list
        HomeScreen homeScreen = new HomeScreen(this, projects);
        this.getChildren().add(homeScreen);
    }

    // Method to switch to ProjectView
    public void switchToProjectView(Project project) {
        ProjectView projectView = new ProjectView(project, this);
        this.getChildren().setAll(projectView);  // Replace current view with ProjectView
    }

    // Method to switch back to HomeScreen
    public void switchToHomeScreen() {
        HomeScreen homeScreen = new HomeScreen(this, projects);
        this.getChildren().setAll(homeScreen);
    }
}
