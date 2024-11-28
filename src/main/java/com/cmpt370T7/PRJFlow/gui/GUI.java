package com.cmpt370T7.PRJFlow.gui;

import com.cmpt370T7.PRJFlow.*;
import com.cmpt370T7.PRJFlow.llm.PdfParser;
import com.cmpt370T7.PRJFlow.llm.PopulateCsv;
import com.cmpt370T7.PRJFlow.llm.PopulateTxt;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class GUI extends BorderPane {

    private final Logger logger = LoggerFactory.getLogger(GUI.class);

    private List<Project> projects;
    private final Map<LocalDate, List<String>> remindersMap;
    private ListView<Project> projectsListView;
    private ProjectFilesPane filesPane;
    private Project selectedProject;
    private Text selectedProjectText;
    private File selectedFile; // selected file
    private Text selectedFileText;

    private CustomCalendar calendar;

    private VBox leftPane, centerPane, rightPane;

    private javafx.scene.Node prevRightChild;

    private FileActionsBox fileActionsBox;
    private RecentProjects recentProjects;

    public GUI() {
        this.setStyle("-fx-background-color: #825B32");
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        this.projects = AppDataManager.getInstance().getConfigManager().getRecentProjects();
        if (projects == null) {
            projects = new ArrayList<>();
        }
        this.remindersMap = AppDataManager.getInstance().getConfigManager().getReminderMap();

        this.selectedProject = null;
        this.selectedFile = null;
        this.calendar = new CustomCalendar(remindersMap);
        this.prevRightChild = calendar;
        this.selectedProjectText = new Text("No Project Selected");
        selectedProjectText.setFont(new Font(20));
        this.selectedFileText = new Text("No File Selected");
        selectedFileText.setFont(new Font(20));
        this.filesPane = new ProjectFilesPane();


        // Set padding for the entire BorderPane
        this.setPadding(new Insets(10));
        // Left pane: Project list
        leftPane = createRecentProjectsPane();
        // Right pane: Calendar and reminders
        rightPane = createRightPane();
        // Center pane: Recent projects and new project button
        centerPane = createCenterPane();
        // Set the panes in the BorderPane
        this.setLeft(leftPane);
        this.setRight(rightPane);
        this.setCenter(centerPane);

        BorderPane.setMargin(leftPane, new Insets(0, 5, 0, 0));   // Gap between left and center
        BorderPane.setMargin(centerPane, new Insets(0, 5, 0, 5)); // Gaps on both sides of center
        BorderPane.setMargin(rightPane, new Insets(0, 0, 0, 5)); // Gap between center and right
    }

    private VBox createRecentProjectsPane() {
        recentProjects = new RecentProjects(projects);
        recentProjects.setOnProjectSelected(() -> this.projectSelection(this.recentProjects.getSelectedProject()));
        recentProjects.setOnNewProject(this::createNewProject);
        recentProjects.setOnDeleteProject(this::deleteProject);

        return recentProjects;
    }

    private VBox createCenterPane() {
        centerPane = new VBox(10);
        centerPane.setPadding(new Insets(10));
        centerPane.setStyle("-fx-background-color: #E5E1DA;");

        HBox curInfoBox = new HBox(10);
        curInfoBox.getChildren().addAll(selectedProjectText, selectedFileText);
        fileActionsBox = createFileActionsBox();
        createFilesPane();

        centerPane.getChildren().addAll(curInfoBox, fileActionsBox, filesPane);

        return centerPane;
    }

    private VBox createRightPane() {
        rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #E5E1DA;");
        rightPane.getChildren().addAll(calendar);
        return rightPane;
    }

    public void revertRightPane() {
        logger.info("Reverting right pane back to previous: {}", prevRightChild);
        rightPane.getChildren().clear();
        rightPane.getChildren().addAll(prevRightChild);
    }

    private void createFilesPane() {
        List<File> projectFiles = selectedProject != null ? selectedProject.getInputFiles() : new ArrayList<>();
        logger.debug("Creating files pane for project: {}", selectedProject);
        this.filesPane.getChildren().clear();
        for (File file : projectFiles) {
            if (file.getName().equals("prjflowconfig.toml")) { continue; }
            ProjectFileButton newButton = createFileButton(file);
            this.filesPane.getChildren().add(newButton);
        }
    }

    private FileActionsBox createFileActionsBox() {
        FileActionsBox fileActionsBox = new FileActionsBox();
        fileActionsBox.setAddFileButtonAction(this::addFile);
        fileActionsBox.setRemoveFileButtonAction(this::removeFile);
        fileActionsBox.setExportButtonAction(this::export);
        fileActionsBox.setSummarizeButtonAction(this::summarize);
        fileActionsBox.setAddDeadlineButton(this::addDeadline);

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
            if (name.trim().isEmpty()) {
                AlertHelper.showError("Invalid Name", "Project name cannot be empty.");
                return;
            }

            // Check if the project is a duplicate
            if (projects.stream().anyMatch(p -> p.getName().equals(name))) {
                AlertHelper.showError("Invalid Name", "A project with that name is already created.");
                return;
            }


            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Choose the project directory");

            File selectedFolder =  dc.showDialog(this.getScene().getWindow());
            if (selectedFolder != null) {
                Project newProject = new Project(name.trim(), selectedFolder);
                projects.addFirst(newProject); // Add to the top of the list
                updateProjectsListView();

                AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                try {
                    ProjectManager.saveProject(newProject, selectedFolder);
                } catch (IOException e) {
                    // TODO handle error on project config file creation
                    throw new RuntimeException(e);
                }

                logger.info("New project created: {}", selectedProject.getName());
                projectSelection(newProject);
            }
        });
    }

    private void deleteProject() {
        if (selectedProject != null) {
            boolean userConfirmation = AlertHelper
                    .showConfirmation("Delete Project",
                            "Are you sure you want to delete the project called: " + selectedProject.getName() + "?");
            if (userConfirmation) {
                projects.remove(selectedProject);
                updateProjectsListView();
                projectSelection(null);
            }
        }
    }

    private ProjectFileButton createFileButton(File file) {
        ProjectFileButton fileButton = new ProjectFileButton(file);

        fileButton.setOnMouseClicked(e -> {
            logger.debug("File button clicked: {}", fileButton.getId());
            if (e.getButton().equals(MouseButton.PRIMARY) && selectedProject != null) {
                if (e.getClickCount() == 1) {
                    setSelectedFile(selectedProject.getFile(fileButton.getId()));

                } else if (e.getClickCount() == 2) {
                    if (getFileExtension(selectedFile.getName()).equals("pdf")) {
                        WebPDFViewer pdfViewer = new WebPDFViewer(file, this);
                        this.rightPane.getChildren().setAll(pdfViewer);
                    } else if (getFileExtension(selectedFile.getName()).equals("txt") || getFileExtension(selectedFile.getName()).equals("csv")) {
                        String summaryString = readFile(selectedFile);
                        System.out.println(summaryString);
                        Text summaryText = new Text(summaryString);
                        Label summaryLabel = new Label(summaryString);
                        summaryLabel.setMinWidth(200);
                        summaryText.wrappingWidthProperty().set(200);
                        summaryLabel.setWrapText(true);
                        summaryLabel.setMaxWidth(300);

                        this.rightPane.getChildren().setAll(summaryLabel);
                    }
                }
            }
        });

        logger.debug("File button created: {}", fileButton.getId());

        return fileButton;
    }

    public String readFile(File file) {
        StringBuilder readString = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String lineString = scanner.nextLine();
                readString.append(lineString);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readString.toString();
    }

    private void updateProjectsListView() {
        recentProjects.getProjectsListView().getItems().setAll(projects);
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

    private void projectSelection(Project project) {
        String projectName = project != null ? project.getName() : "No Project Selected";
        this.selectedProjectText.setText(projectName);
        this.selectedProject = project;
        this.fileActionsBox.hasSelectedProjectProperty().set(project != null);
        createFilesPane();
    }

    private void setSelectedFile(File file) {
        String fileName = file != null ? file.getName() : "No File Selected";
        this.selectedFile = file;

        fileActionsBox.hasSelectedFilesProperty().set(file != null);
        selectedFileText.setText(fileName);
    }

    /*
     * File actions, try and pull these out into a separate class
     */

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
            setSelectedFile(null);
            selectedFileText.setText("No File Selected");
        }
    }

    /* LLM Bullshit */
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
            result.ifPresent(exportFileName -> {
                if (!exportFileName.trim().isEmpty()) {
                    Path exportPath = Paths.get(selectedProject.getDirectory().getPath(), exportFileName + ".csv");
                    File exportFile = exportPath.toFile();
                    selectedProject.addInputFile(exportFile);
                    filesPane.getChildren().add(createFileButton(exportFile));
                    PopulateCsv.GenerateCsv(selectedFile.getAbsolutePath(), exportPath.toString());
                }
            });
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

    private void addDeadline() {
        if (selectedProject == null) {
            AlertHelper.showWarning("No Project Selected", "Please select a project before adding a deadline.");
            return;
        }
        if (selectedFile == null) {
            AlertHelper.showWarning("No File Selected", "Please select a file before adding a deadline.");
            return;
        }

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Deadline");
        dialog.setHeaderText("Select Deadline Date and Enter Reminder");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField reminderField = new TextField();
        reminderField.setPromptText("Reminder");

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Reminder:"), 0, 1);
        grid.add(reminderField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            LocalDate selectedDate = datePicker.getValue();
            String reminderText = reminderField.getText();

            if (reminderText.trim().isEmpty()) {
                AlertHelper.showWarning("Invalid Reminder", "Reminder text cannot be empty.");
                return;
            }

            // current style project, file then reminder
            String formattedReminder = String.format("Project: %s\nFile: %s\n%s",
                    selectedProject.getName(), selectedFile.getName(), reminderText);


            remindersMap.computeIfAbsent(selectedDate, k -> new ArrayList<>()).add(formattedReminder);
            AppDataManager.getInstance().getConfigManager().setReminderMap(remindersMap);


            calendar.updateCalendar();
            calendar.updateReminders();

            logger.info("Added deadline for {}: {}", selectedDate, formattedReminder);
        }
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
                    Path summarizePath = Paths.get(selectedProject.getDirectory().getPath(), summarizeFileName + ".txt");
                    File summarizeFile = summarizePath.toFile();
                    selectedProject.addInputFile(summarizeFile);
                    filesPane.getChildren().add(createFileButton(summarizeFile));
                    PopulateTxt.GenerateTxt(selectedFile.getAbsolutePath(), summarizePath.toString());
                }
            });
        }
    }
}

