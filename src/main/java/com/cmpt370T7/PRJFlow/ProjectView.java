package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectView extends VBox {

    private final MainGUI mainGUI;
    private final List<File> files = new ArrayList<>();

    public ProjectView(MainGUI mainGUI) {
        this.mainGUI = mainGUI;

        this.setStyle("-fx-background-color: #eeeee4");

        // Menu bar setup (optional)
        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        MenuBar menuBar = new MenuBar(fileMenu, viewMenu);
        menuBar.setMinHeight(10);
        VBox.setVgrow(menuBar, Priority.ALWAYS);

        GridPane body = new GridPane();
        body.setPadding(new Insets(10, 5, 10, 5));
        body.setMinHeight(20);
        VBox.setVgrow(body, Priority.SOMETIMES);
        body.setAlignment(Pos.CENTER);
        body.setHgap(5);

        VBox projectInfoBox = new VBox();
        projectInfoBox.getChildren().add(new Text("Project Generated Summary info"));
        body.add(projectInfoBox, 0, 0);

        VBox filesBox = new VBox();
        Button addFileButton = new Button("Add File", new FontIcon("mdi-plus-box"));
        addFileButton.setOnAction(e -> openFileChooser());  // Set up action for Add File button
        filesBox.getChildren().addAll(addFileButton, displayFiles());
        body.add(filesBox, 1, 0);

        VBox fileInfoBox = new VBox();
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

        this.getChildren().addAll(menuBar, body);
    }

    // Method to open file chooser and open selected PDF file
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        // Open the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            // Open the PDF viewer with the selected file
            PDFViewer pdfViewer = new PDFViewer(selectedFile, mainGUI);
            mainGUI.getChildren().setAll(pdfViewer);
        }
    }

    // Display files in the filesBox
    private GridPane displayFiles() {
        GridPane filesPane = new GridPane();
        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(10);
        filesPane.setVgap(10);

        // Since the files list may be empty, no files will be displayed unless added
        return filesPane;
    }
}
