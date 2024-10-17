package com.cmpt370T7.PRJFlow;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;


public class ProjectView extends VBox {

    //Canvas canvas;

    public ProjectView() {
        this.setStyle("-fx-background-color: #eeeee4");


        // Temporary Min Size
        this.setMinHeight(600);
        this.setMinHeight(600);

        ToolBar toolBar = new ToolBar(new Button("File"), new Button("View"));
        toolBar.setMinHeight(20);

        HBox body = new HBox();

        GridPane projectFolders = new GridPane();
        projectFolders.getChildren().add(new Text("Project Folders"));

        GridPane calendar = new GridPane();
        calendar.setStyle("-fx-background-color: #1e81b0");
        calendar.getChildren().add(new Text("Calendar"));

        body.getChildren().addAll(projectFolders, calendar);



        this.getChildren().addAll(toolBar, body);

        // Temporary Code until rest is implemented



        //this.getChildren().add(new BorderPane());
    }
}
