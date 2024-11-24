package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.llm.PopulateCsv;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

@Deprecated // Combined into GUI
public class ProjectView extends VBox {
    private final Logger logger = LoggerFactory.getLogger(ProjectView.class);
    // Temporary variables before code is implemented correctly
    String testCSV = "testInfo.csv";

    private final Project project;
    private final MainGUI mainGUI;
    private final FlowPane filesPane;
    private File selected;



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
        Button exportButton = new Button("Export", new FontIcon("mdi-export"));
        exportButton.setOnAction(e -> export());


        fileActionsBox.getChildren().addAll(addFileButton, removeFileButton, exportButton);

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
        logger.debug("Init files pane");
        filesPane.setStyle("-fx-background-color: #bebeb6");
        filesPane.setPadding(new Insets(10, 10, 10, 10));
        filesPane.setHgap(5);
        filesPane.setVgap(5);


        for (File f : project.getInputFiles()) {
            if (f.getName().equals("prjflowconfig.toml")) { continue; }
            logger.debug("Loaded file: {}", f.getName());
            Button newButton = createFileButton(f);
            filesPane.getChildren().add(newButton);
        }
    }

    // TODO refactor this to a separate classes w/ strategy + with MIME type detection rather than extension
    private Button createFileButton(File file) {
        Button fileButton = new Button();
        FontIcon fileIcon = new FontIcon();

        fileButton.setFont(Font.font("Courier",  11));

        // Determine file extension
        String extension = getFileExtension(file);
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
                    selected = project.getFile(fileButton.getId());
                } else if (e.getClickCount() == 2 && getFileExtension(selected.getName()).equals("pdf")) {
//                    logger.info("Opening PDF file: {}", file);
//                    WebPDFViewer pdfViewer = new WebPDFViewer(file, mainGUI, project);
//                    mainGUI.getChildren().setAll(pdfViewer);
                }
            }
        });

        return fileButton;
    }


    private void addFile() {
        logger.debug("Opening file chooser to add a new file");
        openFileChooser().ifPresent(
            file -> {
                logger.debug("Adding selected file: {}", file);
                project.addInputFile(file);
                filesPane.getChildren().add(createFileButton(file));
            }
        );
    }

    private void removeFile() {
        if (selected != null) {
            project.removeFile(selected.getName());
            filesPane.getChildren().removeIf(f -> (f.getId().equals(selected.getName())));
            selected = null;
        }
    }

    // TODO this is problematic, we don't moderate how much data is being sent, and extractDataElementsFromPdf often returns an empty string.
    private void export() {
        if (selected != null && getFileExtension(selected.getName()).equals("pdf")) {
            String parsedData = PdfParser.extractDataElementsFromPdf(selected.getAbsolutePath());
            String returnedPrompt = PopulateCsv.promptFromData(parsedData);
            PopulateCsv.PasteToCsv("sample-files/TestFiles/TestCsv.csv", returnedPrompt);
        }
    }

    private Optional<File> openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add a new File");
        // Open the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile == null) {
            logger.info("File selection cancelled by user.");
            return Optional.empty();
        } else {
            return Optional.of(selectedFile);
        }
    }

    // TODO remove
    private String getFileExtension(File file) {
        return getFileExtension(file.toString());
    }

    // TODO remove
    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

}
