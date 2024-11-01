package com.cmpt370T7.PRJFlow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PRJFlow extends Application {

    @Override
    public void start(Stage stage) {
        MainGUI root = new MainGUI();
        Scene scene = new Scene(root);
        stage.setTitle("PRJFlow");
        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}
