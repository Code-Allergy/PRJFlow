package com.cmpt370T7.PRJFlow;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.net.URL;


public class ProjectView extends VBox {

    //Canvas canvas;

    String[] files = new String[]{"specs.pdf", "WorkPlan.pdf", "Floorplan.pdf", "ShadeCount.xlsx", "Sched.png"};


    public ProjectView() {
        this.setStyle("-fx-background-color: #eeeee4");


        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        MenuBar menuBar = new MenuBar(fileMenu, viewMenu);
        menuBar.setMinHeight(10);
        VBox.setVgrow(menuBar, Priority.ALWAYS);



        GridPane body = new GridPane();
        body.setMinHeight(20);
        VBox.setVgrow(body, Priority.SOMETIMES);
        body.setAlignment(Pos.CENTER);

        body.setHgap(5);

        GridPane filesPane = new GridPane();
        filesPane.setStyle("-fx-background-color: #bebeb6");
        //TODO Fill gridpane with file icons with filename
        for (int col = 0; col < files.length; col++) {
            filesPane.add(new Text(files[col]), col, 0);
        }

        body.add(filesPane, 0, 0);

        VBox summaryBox = new VBox();
        summaryBox.getChildren().add(new Text("Generated Summary info"));
        body.add(summaryBox, 1, 0);

        ColumnConstraints fileCol = new ColumnConstraints();
        fileCol.setPercentWidth(75);
        ColumnConstraints summaryCol = new ColumnConstraints();
        summaryCol.setPercentWidth(20);
        body.getColumnConstraints().addAll(fileCol, summaryCol);

        RowConstraints bodyRow = new RowConstraints();
        bodyRow.setPercentHeight(900);
        body.getRowConstraints().addAll(bodyRow);



        this.getChildren().addAll(menuBar, body);


    }
}
