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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    String testCSV = "testInfo.csv";

    private Project project;
    private MainGUI mainGUI;
    private String selected = "";
    private FlowPane filesPane;



    public ProjectView(Project project,MainGUI mainGUI) {
        this.project = project;
        this.mainGUI = mainGUI;
        this.selected = null;
        this.filesPane = new FlowPane();
        this.setStyle("-fx-background-color: #f0f0f0");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainGUI.switchToHomeScreen());

        /*
        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        MenuBar menuBar = new MenuBar(fileMenu, viewMenu);
        menuBar.setMinHeight(10);
        VBox.setVgrow(menuBar, Priority.ALWAYS);
        */

        BorderPane body = new BorderPane();
        //body.setPrefHeight(800);
        //body.setPrefWidth(800);
        body.setPadding(new Insets(5, 10, 10, 10));
        //body.setMinHeight(20);
        VBox.setVgrow(body, Priority.ALWAYS);
        //body.setAlignment(Pos.CENTER);
        //body.setHgap(10);

       HBox primaryBox = new HBox();
       primaryBox.setSpacing(5);


        VBox projectInfoBox = new VBox();
        projectInfoBox.setStyle("-fx-background-color: #bebeb6");
        projectInfoBox.getChildren().add(new Text("Project Generated Summary info"));
        //body.getChildren().add(projectInfoBox);


        VBox filesBox = new VBox();
        filesBox.setStyle("-fx-background-color: #bebeb6");

        HBox fileActionsBox = new HBox();
        Button addFileButton = new Button("Add file", new FontIcon("mdi-plus-box"));
        addFileButton.setOnAction(e -> addFile());
        Button removeFileButton = new Button("Remove file", new FontIcon("mdi-delete"));
        removeFileButton.setOnAction(e -> removeFile());
        fileActionsBox.getChildren().addAll(addFileButton, removeFileButton);

        initializeFilesPane();
        filesBox.getChildren().addAll(fileActionsBox, filesPane);
        //body.setCenter(filesBox);

        primaryBox.getChildren().addAll(projectInfoBox, filesBox);
        HBox.setHgrow(projectInfoBox, Priority.ALWAYS);
        HBox.setHgrow(filesBox, Priority.ALWAYS);
        body.setCenter(primaryBox);

        VBox fileInfoBox = new VBox();
        fileInfoBox.setStyle("-fx-background-color: #bebeb6");
        fileInfoBox.getChildren().add(new Text("File Generated Summary Info"));
        body.setRight(fileInfoBox);

        Insets bodyInsets = new Insets(5);
        //BorderPane.setMargin(projectInfoBox, bodyInsets);
        //BorderPane.setMargin(filesBox, bodyInsets);
        BorderPane.setMargin(primaryBox, bodyInsets);
        BorderPane.setMargin(fileInfoBox, bodyInsets);




        Text nameText = new Text("Current Project: " + project.getName());
        nameText.setFont(Font.font("Courier", FontWeight.BOLD, 30));
        VBox.setVgrow(nameText, Priority.NEVER);

        this.getChildren().addAll(backButton, nameText, body);
    }

    private void initializeFilesPane() {
        System.out.println("Init files pane");
        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(5);
        filesPane.setVgap(5);


        for (File f : project.getFiles()) {
            System.out.println("File: " + f.getName());
            Button newButton = createFileButton(f);
            filesPane.getChildren().add(newButton);
        }
    }

    private Button createFileButton(File file) {
        Button fileButton = new Button();
        FontIcon fileIcon = new FontIcon();

        fileButton.setFont(Font.font("Courier",  11));

        // Determine file extension
        String extension = "";
        int i = file.toString().lastIndexOf('.');
        if (i > 0) extension = file.toString().substring(i+1);
        switch (extension) {
            case "pdf" -> fileIcon.setIconLiteral("mdi-file-pdf");
            case "xlsx" -> fileIcon.setIconLiteral("mdi-file-excel");
            case "png", "jpg" -> fileIcon.setIconLiteral("mdi-file-image");
            default -> fileIcon.setIconLiteral("mdi-file");
        }
        fileIcon.setIconSize(30);

        // File name cut off if past 20 characters
        if (file.getName().length() > 20) {
            fileButton.setText(file.getName().substring(0, (20 - extension.length() - 3)) + "..." + extension);
        } else {
            fileButton.setText(file.getName());
        }


        fileButton.setContentDisplay(ContentDisplay.TOP);
        fileButton.setGraphic(fileIcon);
        fileButton.setId(file.getName());
        //fileButton.setPrefSize(30, 30);
        //fileButton.setMinSize(30, 30);
        //fileButton.setMaxSize(40,40);

        fileButton.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 1) {
                    selected = fileButton.getId();
                } else if (e.getClickCount() == 2) {
                    PDFViewer pdfViewer = new PDFViewer(file, mainGUI, project);
                    mainGUI.getChildren().setAll(pdfViewer);
                }
            }
        });

        return fileButton;
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
            filesPane.getChildren().add(createFileButton(newFile));
        }
    }

    private void removeFile() {
        if (selected != null) {
            project.removeFile(selected);
            filesPane.getChildren().removeIf(f -> (f.getId().equals(selected)));
            selected = null;
        }
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

}
