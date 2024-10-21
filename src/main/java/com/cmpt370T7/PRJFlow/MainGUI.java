package com.cmpt370T7.PRJFlow;

import javafx.scene.layout.StackPane;

public class MainGUI extends StackPane {

    public MainGUI() {
        this.setPrefSize(800, 600);
        //HomeScreen homeScreen = new HomeScreen();
        //this.getChildren().add(homeScreen);

        ProjectView projectView = new ProjectView();
        this.getChildren().add(projectView);

    }
}
