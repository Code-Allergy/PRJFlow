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

import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;


public class ProjectView extends VBox {

    // Temporary variables before code is implemented correctly
    String[] files = new String[]{"specs.pdf", "WorkPlan.pdf", "Floorplan.pdf", "ShadeCount.xlsx", "Sched.png"};
    String testCSV = "testInfo.csv";
    Project project;
    private MainGUI mainGUI;
    GridPane filesPane = new GridPane();

    String selected = "";
    boolean removeMode = false;





    public ProjectView(Project project,MainGUI mainGUI) {
        this.project = project;
        this.mainGUI = mainGUI;
        this.setStyle("-fx-background-color: #eeeee4");
        this.setAlignment(Pos.TOP_LEFT);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainGUI.switchToHomeScreen());

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
        HBox fileButtonsBox = new HBox();

        Button addFileButton = new Button("Add file", new FontIcon("mdi-plus-box"));
        addFileButton.setOnAction(e -> addFile());
        Button removeFileButton = new Button("Remove file", new FontIcon("mdi-delete"));
        removeFileButton.setOnAction(e -> {
            removeMode = true;
        });
        fileButtonsBox.getChildren().addAll(addFileButton, removeFileButton);

        this.filesPane = displayFiles();
        filesBox.getChildren().addAll(fileButtonsBox, filesPane);

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
        this.getChildren().addAll(backButton, nameText, body);
    }


    void addFile() {
        System.out.println("Add a new file");
        project.addFile(openFileChooser());
        System.out.println(project.getFiles());
        displayFiles();
    }

    void removeFile() {
        System.out.println(selected);
        project.removeFile(selected);
        displayFiles();
        removeMode = false;
    }

    GridPane displayFiles() {
        filesPane.getChildren().clear();

        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(10);
        filesPane.setVgap(10);

        for (int col = 0; col < project.getFiles().size(); col++) {
            File curFile = project.getFiles().get(col);

            VBox fileBox = new VBox();

            FontIcon fileIcon = new FontIcon();
            String extension = "";

            int i = curFile.toString().lastIndexOf('.');
            if (i > 0) extension = curFile.toString().substring(i+1);
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

            Button fileButton = new Button();
            fileButton.setGraphic(fileIcon);
            fileButton.setPrefHeight(30);
            fileButton.setId(curFile.getName());
            fileButton.setOnAction(e -> {
                selected = fileButton.getId();
                if (removeMode) {
                    removeFile();
                    removeMode = false;
                } else {
                    PDFViewer pdfViewer = new PDFViewer(curFile, mainGUI, project);
                    mainGUI.getChildren().setAll(pdfViewer);
                }


            });

            fileBox.getChildren().addAll(fileButton, new Text(curFile.getName()));
            filesPane.add(fileBox, col, 0);
        }
        return filesPane;
    }

    File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add a new File");
        // Open the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            return selectedFile;
        } else {
            System.out.println("Error: no file found");
            return null;
        }
    }
}
