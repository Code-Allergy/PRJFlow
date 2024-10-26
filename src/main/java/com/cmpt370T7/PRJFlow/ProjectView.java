package com.cmpt370T7.PRJFlow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;


public class ProjectView extends VBox {

    // Temporary variables before code is implemented correctly
    String[] files = new String[]{"specs.pdf", "WorkPlan.pdf", "Floorplan.pdf", "ShadeCount.xlsx", "Sched.png"};
    String testCSV = "testInfo.csv";
    Project project;



    public ProjectView(Project project) {
        this.project = project;
        this.setStyle("-fx-background-color: #eeeee4");
        this.setAlignment(Pos.CENTER);

        /*
        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        MenuBar menuBar = new MenuBar(fileMenu, viewMenu);
        menuBar.setMinHeight(10);
        VBox.setVgrow(menuBar, Priority.ALWAYS);
        */

        GridPane body = new GridPane();
        body.setPrefHeight(800);
        body.setPadding(new Insets(5, 10, 10, 10));
        body.setMinHeight(20);
        VBox.setVgrow(body, Priority.ALWAYS);
        body.setAlignment(Pos.CENTER);
        body.setHgap(10);


        VBox projectInfoBox = new VBox();
        projectInfoBox.setStyle("-fx-background-color: #bebeb6");
        projectInfoBox.getChildren().add(new Text("Project Generated Summary info"));
        body.add(projectInfoBox, 0, 0);

        VBox filesBox = new VBox();
        filesBox.setStyle("-fx-background-color: #bebeb6");
        Button addFileButton = new Button("Add file", new FontIcon("mdi-plus-box"));

        addFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //File Chooser
            }
        });

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
        bodyRow.setPercentHeight(90);
        body.getRowConstraints().addAll(bodyRow);


        Text nameText = new Text("Current Project: " + project.getName());
        VBox.setVgrow(nameText, Priority.NEVER);
        this.getChildren().addAll(nameText, body);
    }




    GridPane displayFiles() {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("Hello.txt");

        GridPane filesPane = new GridPane();
        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(10);
        filesPane.setVgap(10);

        for (int col = 0; col < fileNames.size(); col++) {

            VBox fileBox = new VBox();

            FontIcon fileIcon = new FontIcon();
            String extension = "";
            int i = fileNames.get(col).lastIndexOf('.');
            if (i > 0) extension = fileNames.get(col).substring(i+1);
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
                default:
                    fileIcon.setIconLiteral("mdi-file");
            }

            //Button fileButton = new Button(fileNames.get(col), fileIcon);
            Button fileButton = new Button();
            fileButton.setGraphic(fileIcon);
            fileButton.setPrefHeight(30);

            fileBox.getChildren().addAll(fileButton, new Text("TEST"));
            filesPane.add(fileBox, col, 0);
        }
        return filesPane;
    }
}
