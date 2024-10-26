package com.cmpt370T7.PRJFlow;

import javafx.scene.layout.StackPane;

public class MainGUI extends StackPane {

    public MainGUI() {
        this.setPrefSize(800, 600);

        HomeScreen homeScreen = new HomeScreen(this);
        this.getChildren().add(homeScreen);
    }

    // Method to switch to ProjectView
    public void switchToProjectView() {
        ProjectView projectView = new ProjectView(this);
        this.getChildren().setAll(projectView);  // Replace current view with ProjectView
    }

    public void switchToHomeScreen() {
        HomeScreen homeScreen = new HomeScreen(this);
        this.getChildren().setAll(homeScreen);
    }
}
