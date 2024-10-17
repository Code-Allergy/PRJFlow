package com.cmpt370T7.PRJFlow;

import javafx.scene.layout.StackPane;

public class MainGUI extends StackPane {

    public MainGUI() {
        HomeScreen homeScreen = new HomeScreen();
        this.getChildren().add(homeScreen);
    }
}
