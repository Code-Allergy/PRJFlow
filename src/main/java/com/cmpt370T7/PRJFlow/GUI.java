package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.llm.PdfParser;
import com.cmpt370T7.PRJFlow.llm.PopulateCsv;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class GUI extends BorderPane {

    // TODO FIX LOGGER
    private final Logger logger = LoggerFactory.getLogger(GUI.class);
    private List<Project> projects;
    private final Map<LocalDate, List<String>> remindersMap;
    private ListView<Project> projectsListView;
    private FlowPane filesPane;
    private Project selectedProject;
    private Text selectedProjectText;
    private File selectedFile; // selected file
    private Text selectedFileText;

    private CustomCalendar calendar;

    private VBox leftPane, centerPane, rightPane;

    private javafx.scene.Node prevRightChild;

    public GUI() {
        this.setStyle("-fx-background-color: #f0f0f0");
        this.projects = AppDataManager.getInstance().getConfigManager().getRecentProjects();
        if (projects == null) {
            projects = new ArrayList<>();
        }
        this.remindersMap = AppDataManager.getInstance().getConfigManager().getReminderMap();

        this.selectedProject = null;
        this.selectedFile = null;
        this.filesPane = new FlowPane();
        this.calendar = new CustomCalendar(remindersMap);
        this.prevRightChild = calendar;
        this.selectedProjectText = new Text("No Project Selected");
        selectedProjectText.setFont(new Font(20));
        this.selectedFileText = new Text("No File Selected");
        selectedFileText.setFont(new Font(20));


        // Set padding for the entire BorderPane
        this.setPadding(new Insets(10));
        // Left pane: Project list
        leftPane = createLeftPane();
        // Right pane: Calendar and reminders
        rightPane = createRightPane();
        // Center pane: Recent projects and new project button
        centerPane = createCenterPane();
        // Set the panes in the BorderPane
        this.setLeft(leftPane);
        this.setRight(rightPane);
        this.setCenter(centerPane);
    }

    private VBox createLeftPane() {
        leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-background-color: #f0f0f0;");

        Label projectsLabel = new Label("Projects");
        projectsListView = new ListView<>();
        updateProjectsListView();

        // Handle project selection
        projectsListView.setOnMouseClicked(event -> {
            selectedProject = projectsListView.getSelectionModel().getSelectedItem();
            projectSelection();
        });

        Button newProjectButton = new Button("New Project");
        // Set action for new project button to create a new project
        newProjectButton.setOnAction(e -> createNewProject());

        Button deleteProjectButton = new Button("Delete Project");
        deleteProjectButton.setOnAction(e -> deleteProject());

        leftPane.getChildren().addAll(projectsLabel, projectsListView, newProjectButton, deleteProjectButton);
        return leftPane;
    }

    private VBox createCenterPane() {
        centerPane = new VBox(10);
        centerPane.setPadding(new Insets(10));
        centerPane.setStyle("-fx-background-color: #f0f0f0;");

        HBox curInfoBox = new HBox(10);
        curInfoBox.getChildren().addAll(selectedProjectText, selectedFileText);
        HBox fileActionsBox = createFileActionsBox();
        createFilesPane();

        centerPane.getChildren().addAll(curInfoBox, fileActionsBox, filesPane);

        return centerPane;
    }

    private VBox createRightPane() {
        rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #f0f0f0;");
        rightPane.getChildren().addAll(calendar);
        return rightPane;
    }

    public void revertRightPane() {
        logger.info("Reverting right pane back to previous: {}", prevRightChild);
        rightPane.getChildren().clear();
        rightPane.getChildren().addAll(prevRightChild);
    }

    private void createFilesPane() {
        this.filesPane.getChildren().clear();
        filesPane.setStyle("-fx-background-color: #ffffff;");
        filesPane.setPadding(new Insets(10));
        filesPane.setHgap(5);
        filesPane.setVgap(5);

        if (selectedProject != null) {
            logger.info("Loading files for project: {}", selectedProject.getName());
            for (File f : selectedProject.getInputFiles()) {
                if (f.getName().equals("prjflowconfig.toml")) { continue; }
                logger.debug("Loaded file: {}", f.getName());
                Button newButton = createFileButton(f);
                filesPane.getChildren().add(newButton);
            }
        }

    }

    private HBox createFileActionsBox() {
        HBox fileActionsBox = new HBox();
        fileActionsBox.setAlignment(Pos.CENTER_LEFT);

        Button addFileButton = new Button("Add file", new FontIcon("mdi-plus-box"));
        addFileButton.setOnAction(e -> addFile());
        Button removeFileButton = new Button("Remove file", new FontIcon("mdi-delete"));
        removeFileButton.setOnAction(e -> removeFile());
        Button exportButton = new Button("Export", new FontIcon("mdi-export"));
        exportButton.setOnAction(e -> export());
        Button summarizeButton = new Button("Summarize", new FontIcon("mdi-creation"));
        summarizeButton.setOnAction(e -> summarize());

        fileActionsBox.getChildren().addAll(addFileButton, removeFileButton, exportButton, summarizeButton);
        return fileActionsBox;
    }

    private void createNewProject() {
        // Prompt the user for a project name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Project Name:");

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Name");
        alert.setHeaderText(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                boolean duplicateProject = false;
                for (Project p : projects) {
                    if (p.getName().equals(name)) {
                        duplicateProject = true;
                        break;
                    }
                }
                if (!duplicateProject) {
                    DirectoryChooser dc = new DirectoryChooser();
                    dc.setTitle("Choose the project directory");

                    File selectedFolder =  dc.showDialog(this.getScene().getWindow());
                    if (selectedFolder != null) {
                        Project newProject = new Project(name.trim(), selectedFolder);
                        projects.addFirst(newProject); // Add to the top of the list
                        updateProjectsListView();
                        selectedProject = newProject;
                        logger.info("New project created: {}", selectedProject.getName());
                        AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                        try {
                            ProjectManager.saveProject(newProject, selectedFolder);
                        } catch (IOException e) {
                            // TODO handle error on project config file creation
                            throw new RuntimeException(e);
                        }

                        projectSelection();
                    }
                } else {
                    alert.setContentText("A project with that name is already created.");
                    alert.showAndWait();
                }
            } else {
                alert.setContentText("Project name cannot be empty.");
                alert.showAndWait();
            }
        });
    }

    private void deleteProject() {
        if (selectedProject != null) {
            Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete ", ButtonType.YES, ButtonType.NO);
            deleteConfirmation.setContentText("Are you sure you want to delete the project called: " + selectedProject.getName() + "?");
            deleteConfirmation.showAndWait();
            if (deleteConfirmation.getResult() == ButtonType.YES) {
                projects.remove(selectedProject);
                selectedProject = null;
                updateProjectsListView();
                projectSelection();
            }
        }
    }

    private Button createFileButton(File file) {
        Button fileButton = new Button();
        FontIcon fileIcon = new FontIcon();

        fileButton.setFont(Font.font("Courier",  11));

        // Determine file extension
        String extension = getFileExtension(file);
        switch (extension) {
            case "pdf" -> fileIcon.setIconLiteral("mdi-file-pdf");
            case "xlsx", "csv" -> fileIcon.setIconLiteral("mdi-file-excel");
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

        fileButton.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && selectedProject != null) {
                if (e.getClickCount() == 1) {
                    selectedFile = selectedProject.getFile(fileButton.getId());
                    selectedFileText.setText(selectedFile.getName());
                } else if (e.getClickCount() == 2 && getFileExtension(selectedFile.getName()).equals("pdf")) {
                    WebPDFViewer pdfViewer = new WebPDFViewer(file, this);
                    //this.setRight(pdfViewer);
                    this.rightPane.getChildren().setAll(pdfViewer);
                }
            }
        });
        return fileButton;
    }

    private void addFile() {
        if (selectedProject == null) {
            return;
        }
        logger.debug("Opening file chooser to add a new file");
        openFileChooser().ifPresent(
                file -> {
                    logger.debug("Adding selected file: {}", file);
                    if (selectedProject.contains(file)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid File");
                        alert.setHeaderText(null);
                        alert.setContentText("A file with that name is already in the project.");
                        alert.showAndWait();
                    } else {
                        selectedProject.addInputFile(file);
                        filesPane.getChildren().add(createFileButton(file));
                    }
                }
        );
    }

    private void removeFile() {
        if (selectedProject == null) {
            return;
        }
        if (selectedFile != null) {
            logger.info("Removing file from project: {}", selectedFile.getName());
            selectedProject.removeFile(selectedFile.getName());
            filesPane.getChildren().removeIf(f -> (f.getId().equals(selectedFile.getName())));
            selectedFile = null;
            selectedFileText.setText("No File Selected");
        }
    }
    private void updateProjectsListView() {
        projectsListView.getItems().setAll(projects);
    }

    private void export() {
        if (selectedProject == null) {
            return;
        }
        if (selectedFile != null && getFileExtension(selectedFile.getName()).equals("pdf")) {
            TextInputDialog exportDialog = new TextInputDialog();
            exportDialog.setTitle("Export");
            exportDialog.setHeaderText("Output file");
            exportDialog.setContentText("Enter the name of the output file:");

            Optional<String> result = exportDialog.showAndWait();

            //Had to comment this out Generatetxt and GenerateCsv in
            //Populate classes in llm have functions nessecary for this to work

            result.ifPresent(exportFileName -> {
                //if (!exportFileName.trim().isEmpty()) {
                    //String parsedData = PdfParser.pa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        extractDataElementsFromPdf(selectedFile.getAbsolutePath());
                    //String returnedPrompt = PopulateCsv.promptFromData(parsedData);
                    // TODO EXPORT PATH
                    //String exportPath = "sample-files/TestFiles/" + exportFileName;
                    //PopulateCsv.PasteToCsv(exportPath, returnedPrompt);
                //}
            });

            //TODO: PopulatePdf.GenerateTxt(); also PopulateCsv.GenerateCsv();
        }
    }

    private String getFileExtension(File file) {
        return getFileExtension(file.toString());
    }

    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
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

    private void projectSelection() {
        if (selectedProject != null) {
            selectedProjectText.setText(selectedProject.getName());
        } else {
            selectedProjectText.setText("No Project Selected");
        }
        createFilesPane();
    }

    private void summarize() {
        if (selectedProject == null) {
            return;
        }
        if (selectedFile != null && getFileExtension(selectedFile.getName()).equals("pdf")) {
            TextInputDialog  summarizeDialog = new TextInputDialog();
            summarizeDialog.setTitle("Summarize");
            summarizeDialog.setHeaderText("Output file");
            summarizeDialog.setContentText("Enter the name of the output file:");

            Optional<String> result =  summarizeDialog.showAndWait();
            result.ifPresent(summarizeFileName -> {
                if (!summarizeFileName.trim().isEmpty()) {
                    String exportPath = "sample-files/TestFiles/" + summarizeFileName;
                }
            });
        }
    }



}
