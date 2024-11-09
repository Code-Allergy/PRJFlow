package com.cmpt370T7.PRJFlow;

import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MainGUI extends StackPane {

    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);
    private List<Project> projects;
    private ProjectView pView;
    private HomeScreen homeScreen;

    public MainGUI() {
        //this.setPrefSize(800, 600);

        // Initialize the list of projects if no config exists
        projects = AppDataManager.getInstance().getConfigManager().getRecentProjects();
        if (projects == null) {
            projects = new ArrayList<>();
        }

        // Create HomeScreen and pass the projects list
        homeScreen = new HomeScreen(this, projects);
        this.getChildren().add(homeScreen);
    }

    // Method to switch to ProjectView
    public void switchToProjectView(Project project) {
        //this.setPrefSize(800, 600);
        //this.setWidth(800);
        //this.setHeight(600);
        pView = new ProjectView(project, this);
        this.getChildren().setAll(pView);  // Replace current view with ProjectView
    }

    // Method to switch back to HomeScreen
    public void switchToHomeScreen() {
        this.getChildren().setAll(homeScreen);
    }
}
