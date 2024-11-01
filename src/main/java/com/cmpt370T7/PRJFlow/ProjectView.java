package com.cmpt370T7.PRJFlow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
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

    //Project selected;
    String selected = "";
    boolean removeMode = false;





    public ProjectView(Project project,MainGUI mainGUI) {
        this.project = project;
        this.mainGUI = mainGUI;
        this.selected = null;
        this.setStyle("-fx-background-color: #f0f0f0");
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
        //body.setPrefHeight(800);
        //body.setPrefWidth(800);
        body.setPadding(new Insets(5, 10, 10, 10));
        //body.setMinHeight(20);
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
            if (selected != null) {
                removeFile();
                selected = null;
            }
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

    // Add files that are in projects directory
    private void initialFiles() {
        System.out.println("Add initial files");

    }

    private void addFileToProject(File f) {
        System.out.println("Add a new file to project");
        project.addFile(f);
    }




    private void addFile() {
        System.out.println("Add a new file");
        File newFile = openFileChooser();
        if (newFile != null) {
            project.addFile(newFile);
            displayFiles();
        }
    }

    private void removeFile() {
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

            Button fileButton = new Button();
            FontIcon fileIcon = new FontIcon();

            // Determine file extension
            String extension = "";
            int i = curFile.toString().lastIndexOf('.');
            if (i > 0) extension = curFile.toString().substring(i+1);
            switch (extension) {
                case "pdf" -> fileIcon.setIconLiteral("mdi-file-pdf");
                case "xlsx" -> fileIcon.setIconLiteral("mdi-file-excel");
                case "png", "jpg" -> fileIcon.setIconLiteral("mdi-file-image");
                default -> fileIcon.setIconLiteral("mdi-file");
            }
            fileIcon.setIconSize(30);

            // File name cut off if past 20 characters
            if (curFile.getName().length() > 20) {
                fileButton.setText(curFile.getName().substring(0, (20 - extension.length() - 3)) + "..." + extension);
            } else {
                fileButton.setText(curFile.getName());
            }


            fileButton.setContentDisplay(ContentDisplay.TOP);
            fileButton.setGraphic(fileIcon);
            fileButton.setId(curFile.getName());

            fileButton.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (e.getClickCount() == 1) {
                        selected = fileButton.getId();
                    } else if (e.getClickCount() == 2) {
                        PDFViewer pdfViewer = new PDFViewer(curFile, mainGUI, project);
                        mainGUI.getChildren().setAll(pdfViewer);
                    }
                }
            });

            filesPane.add(fileButton, col, 0);
        }
        return filesPane;
    }



    private File openFileChooser() {
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

    /*
    private void fileClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 1) {
                selected =
            } else if (event.getClickCount() == 2) {

            }
        }
    }*/
}
