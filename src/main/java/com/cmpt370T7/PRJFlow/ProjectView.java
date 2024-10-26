package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.net.URL;


public class ProjectView extends VBox {

    // Temporary variables before code is implemented correctly
    String[] files = new String[]{"specs.pdf", "WorkPlan.pdf", "Floorplan.pdf", "ShadeCount.xlsx", "Sched.png"};
    String testCSV = "testInfo.csv";



    public ProjectView() {
        this.setStyle("-fx-background-color: #eeeee4");

        /*
        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        MenuBar menuBar = new MenuBar(fileMenu, viewMenu);
        menuBar.setMinHeight(10);
        VBox.setVgrow(menuBar, Priority.ALWAYS);
        */

        GridPane body = new GridPane();
        body.setPrefHeight(800);
        body.setPadding(new Insets(10, 5, 10, 5));
        body.setMinHeight(20);
        //VBox.setVgrow(body, Priority.SOMETIMES);
        body.setAlignment(Pos.CENTER);
        body.setHgap(5);


        VBox projectInfoBox = new VBox();
        projectInfoBox.setStyle("-fx-background-color: #bebeb6");
        projectInfoBox.getChildren().add(new Text("Project Generated Summary info"));
        body.add(projectInfoBox, 0, 0);

        VBox filesBox = new VBox();
        filesBox.setStyle("-fx-background-color: #bebeb6");
        Button addFileButton = new Button("Add file", new FontIcon("mdi-plus-box"));
        filesBox.getChildren().addAll(addFileButton, displayFiles());
        body.add(filesBox, 1, 0);

        VBox fileInfoBox = new VBox();
        fileInfoBox.setStyle("-fx-background-color: #bebeb6");
        fileInfoBox.getChildren().add(new Text("File Generated Summary Info"));
        body.add(fileInfoBox, 2, 0);


        ColumnConstraints projectSummaryCol = new ColumnConstraints();
        projectSummaryCol.setPercentWidth(15);
        ColumnConstraints filesCol = new ColumnConstraints();
        filesCol.setPercentWidth(70);
        ColumnConstraints fileSummaryCol = new ColumnConstraints();
        fileSummaryCol.setPercentWidth(15);
        body.getColumnConstraints().addAll(projectSummaryCol, filesCol, fileSummaryCol);

        RowConstraints bodyRow = new RowConstraints();
        bodyRow.setPercentHeight(900);
        body.getRowConstraints().addAll(bodyRow);

        this.getChildren().addAll(body);
    }


    GridPane displayFiles() {

        GridPane filesPane = new GridPane();
        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(10);
        filesPane.setVgap(10);

        for (int col = 0; col < files.length; col++) {

            String extension = "";
            int i = files[col].lastIndexOf('.');
            if (i > 0) extension = files[col].substring(i+1);

            FontIcon fileIcon = new FontIcon();
            switch (extension) {
                case "pdf":
                    fileIcon.setIconLiteral("mdi-file-pdf");
                    break;
                case "xlsx":
                    fileIcon.setIconLiteral("mdi-file-excel");
                    break;
                case "png", "jpg":
                    fileIcon.setIconLiteral("mdi-file-image");
                    break;
            }

            Button fileButton = new Button(files[col], fileIcon);
            fileButton.setPrefHeight(30);
            filesPane.add(fileButton, col, 0);
        }
        return filesPane;
    }
}
