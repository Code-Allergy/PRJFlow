package com.cmpt370T7.PRJFlow.gui;

import com.cmpt370T7.PRJFlow.*;
import com.cmpt370T7.PRJFlow.llm.PdfParser;
import com.cmpt370T7.PRJFlow.llm.PopulateCsv;
import com.cmpt370T7.PRJFlow.llm.PopulateTxt;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class GUI extends BorderPane {

    private final Logger logger = LoggerFactory.getLogger(GUI.class);

    private List<Project> projects;
    private final Map<LocalDate, List<String>> remindersMap;
    private ProjectFilesPane filesPane;
    private Project selectedProject;
    private Text selectedProjectText;
    private File selectedFile; // selected file
    private Text selectedFileText;
    private CustomCalendar calendar;
    private VBox leftPane, centerPane, rightPane;
    private FileActionsBox fileActionsBox;
    private RecentProjects recentProjects;
    private Label summaryLabel;
    private HBox rightPaneButtonBox;

    /**
     * Constructor for GUI extending BorderPane. The main frontend file that the user interacts with.
     */
    public GUI() {
        this.setStyle("-fx-background-color: #1A1A1D");
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        this.projects = AppDataManager.getInstance().getConfigManager().getRecentProjects();
        if (projects == null) {
            projects = new ArrayList<>();
        }
        this.remindersMap = AppDataManager.getInstance().getConfigManager().getReminderMap();

        this.selectedProject = null;
        this.selectedFile = null;
        this.calendar = new CustomCalendar(remindersMap);
        this.selectedProjectText = new Text("No Project Selected");
        selectedProjectText.setFont(new Font(20));
        this.selectedFileText = new Text("No File Selected");
        selectedFileText.setFont(new Font(20));
        this.filesPane = new ProjectFilesPane();
        this.summaryLabel = null;
        this.rightPaneButtonBox = new HBox();


        // Set padding for the entire BorderPane
        this.setPadding(new Insets(10));
        // Left pane: Project list
        leftPane = createRecentProjectsPane();
        // Right pane: Calendar and reminders
        rightPane = createRightPane();
        rightPane.setMinWidth(400);
        rightPane.setMaxWidth(600);
        rightPane.setPrefWidth(400);

        // Center pane: Recent projects and new project button
        centerPane = createCenterPane();
        // Set the panes in the BorderPane
        this.setLeft(leftPane);
        this.setRight(rightPane);
        this.setCenter(centerPane);

        BorderPane.setMargin(leftPane, new Insets(0, 5, 0, 0));   // Gap between left and center
        BorderPane.setMargin(centerPane, new Insets(0, 5, 0, 5)); // Gaps on both sides of center
        BorderPane.setMargin(rightPane, new Insets(0, 0, 0, 5)); // Gap between center and right
        BorderPane.setAlignment(rightPane, Pos.CENTER_RIGHT);
    }

    /**
     * Creates the left pane that displays the projects. Primarily contains the ListView of projects
     * with interactive buttons beneath it
     * @return The recentProjects VBox
     */
    private VBox createRecentProjectsPane() {
        recentProjects = new RecentProjects(projects);
        recentProjects.setOnProjectSelected(() -> this.projectSelection(this.recentProjects.getSelectedProject()));
        recentProjects.setOnNewProject(this::createNewProject);
        recentProjects.setOnDeleteProject(this::deleteProject);
        recentProjects.setOnEditProjectName(this::editProjectName);
        recentProjects.setOnContextEditProjectName(this::editProjectName);
        recentProjects.setOnContextDeleteProjectName(this::deleteProject);

        return recentProjects;
    }

    /**
     * Creates the center pane that displays the files. Primarily contains the FlowPane of files with
     * the interactive buttons above it.
     * @return The centerPane VBox
     */
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

    /**
     * Creates the right pane that displays either the Calendar, PDFView, or Text summary.
     * Only one of these is displayed at any time.
     * @return The rightPane VBox
     */
    private VBox createRightPane() {
        rightPane = new VBox(10);

        Button calendarButton = new Button("Calendar");
        calendarButton.getStyleClass().add("accent-button");
        calendarButton.setOnAction(a -> viewCalendar());

        Button viewPdfButton = new Button("ViewPDF");
        viewPdfButton.getStyleClass().add("accent-button");
        viewPdfButton.setOnAction(a -> viewPDF());

        Button viewTxtButton = new Button("ViewTxt");
        viewTxtButton.getStyleClass().add("accent-button");
        viewTxtButton.setOnAction(a -> viewTXT());

        rightPaneButtonBox.setSpacing(5);
        rightPaneButtonBox.getChildren().addAll(calendarButton, viewPdfButton, viewTxtButton);

        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #E5E1DA;");
        rightPane.getChildren().addAll(rightPaneButtonBox,calendar);
        return rightPane;
    }


    /**
     * Initializes the filesPane by creating each fileButton for the selected project.
     * If there is no selected project, there are simply no fileButtons.
     */
    private void createFilesPane() {
        List<File> projectFiles = new ArrayList<>();
        if (selectedProject != null) {
            projectFiles.addAll(selectedProject.getInputFiles());
            projectFiles.addAll(selectedProject.getSummaryFiles());
        }

        logger.debug("Creating files pane for project: {}", selectedProject);
        // Remove old project files from pane
        this.filesPane.getChildren().clear();
        // Create a file button for every file except the .toml
        for (File file : projectFiles) {
            if (file.getName().equals("prjflowconfig.toml")) {
                continue;
            }
            ProjectFileButton newButton = createFileButton(file);
            this.filesPane.getChildren().add(newButton);
        }
    }

    /**
     * Create the HBox of buttons related to file interaction.
     * @return The FileActionsBox which extends HBox
     */
    private FileActionsBox createFileActionsBox() {
        FileActionsBox fileActionsBox = new FileActionsBox();
        fileActionsBox.setAddFileButtonAction(this::addFile);
        fileActionsBox.setRemoveFileButtonAction(this::removeFile);
        fileActionsBox.setExportButtonAction(this::export);
        fileActionsBox.setSummarizeButtonAction(this::summarize);
        fileActionsBox.setAddDeadlineButton(this::addDeadline);

        return fileActionsBox;
    }


    /**
     * Prompt the user through interactive dialog to create a new project at a chosen directory.
     */
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
            Path defaultPath = Paths.get(System.getProperty("user.home"), "Documents");
            File defaultDirectory = defaultPath.toFile();
            if (!defaultDirectory.exists()) {
                defaultDirectory = new File(System.getProperty("user.home"));
            }

            dc.setInitialDirectory(defaultDirectory);

            File selectedFolder = dc.showDialog(this.getScene().getWindow());
            if (selectedFolder != null) {
                Project newProject = new Project(name.trim(), selectedFolder);
                newProject.addInitialFiles();
                addProject(newProject); // Add to the top of the list

                AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                // Add the project to the programs config file
                updateConfig(newProject, selectedFolder);

                logger.info("New project created: {}", selectedProject.getName());
                projectSelection(newProject);
            }
        });
    }

    /**
     * Add a given project to the list of projects.
     * This does not require choosing the name and location like createNewProject().
     * @param project A project to add to list of projects
     */
    public void addProject(Project project) {
        projects.addFirst(project);
        // Make sure ListView in left pane is updated
        updateProjectsListView();
        selectedProject = project;
    }

    /**
     * Delete the current selectedProject.
     * If there is no selectedProject, nothing happens.
     */
    private void deleteProject() {
        if (selectedProject != null) {
            boolean userConfirmation = AlertHelper
                    .showConfirmation("Delete Project",
                            "Are you sure you want to delete the project called: " + selectedProject.getName() + "?");
            if (userConfirmation) {
                projects.remove(selectedProject);
                updateProjectsListView();
                // Remove the project from the programs config file
                try {
                    ProjectManager.removeProject(selectedProject);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                projectSelection(null);
            }
        }
    }

    /**
     * Change the name of the current selectedProject.
     */
    private void editProjectName() {
        if (selectedProject != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Edit Project Name");
            dialog.setHeaderText("Enter the new project name");
            dialog.setContentText("Project Name:");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Name");
            alert.setHeaderText(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    AlertHelper.showError("Invalid Name", "Project name cannot be empty.");
                } else if (projects.stream().anyMatch(p -> p.getName().equals(name))) {
                    AlertHelper.showError("Invalid Name", "A project with that name is already created.");
                } else {
                    selectedProject.setName(name);
                    updateConfig(selectedProject, selectedProject.getDirectory());
                    AppDataManager.getInstance().getConfigManager().setRecentProjects(projects);
                    updateProjectsListView();
                    logger.info("Project changed name, new name: {}", selectedProject.getName());
                    projectSelection(selectedProject);
                }
            });
        }
    }

    /**
     * Create a JavaFX button for each file. These will be displayed and interactive in the center pane.
     * Single click selects the file, double click attempts to view the file in the right pane.
     * @param file A file to represent as a Button
     * @return A ProjectFileButton that extends Button
     */
    private ProjectFileButton createFileButton(File file) {
        ProjectFileButton fileButton = new ProjectFileButton(file);

        // Set behaviour on button click
        fileButton.setOnMouseClicked(e -> {
            logger.debug("File button clicked: {}", fileButton.getId());
            if (e.getButton().equals(MouseButton.PRIMARY) && selectedProject != null) {
                if (e.getClickCount() == 1) {
                    setSelectedFile(selectedProject.getFile(fileButton.getId()));
                } else if (e.getClickCount() == 2) {
                    if (getFileExtension(selectedFile.getName()).equals("pdf")) {
                        viewPDF();
                    } else if (getFileExtension(selectedFile.getName()).equals("txt") || getFileExtension(selectedFile.getName()).equals("csv")) {
                        viewTXT();
                    }
                }
            }
        });

        logger.debug("File button created: {}", fileButton.getId());
        return fileButton;
    }

    /**
     * If a file is selected and it is a pdf, a PDF viewer is created and displayed in the right pane.
     */
    public void viewPDF() {
        if (selectedFile != null && getFileExtension(selectedFile.getName()).equals("pdf")) {
            WebPDFViewer pdfViewer = new WebPDFViewer(selectedFile, this);
            this.rightPane.getChildren().setAll(rightPaneButtonBox, pdfViewer);
            // Give the pdfViewer more space than calendar
            rightPane.setMinWidth(600);
            rightPane.setMaxWidth(600);
            rightPane.setPrefWidth(600);
        }
    }

    /**
     * Display the calendar in the right pane
     */
    public void viewCalendar() {
        this.rightPane.getChildren().setAll(rightPaneButtonBox, calendar);
        // Give the calendar less space than pdfViewer or text display
        rightPane.setMinWidth(400);
        rightPane.setMaxWidth(600);
        rightPane.setPrefWidth(400);
    }

    /**
     * If a file is selected and it is a txt or csv, a text display is created and displayed in the right pane.
     */
    public void viewTXT() {
        if (selectedFile != null) {
            if (getFileExtension(selectedFile.getName()).equals("txt") ||
                    getFileExtension(selectedFile.getName()).equals("csv")) {
                // Read the contents of the selected file
                String summaryString = "";
                try {
                    summaryString = readFile(selectedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // A label is how text is displayed
                summaryLabel = new Label(summaryString);
                summaryLabel.setWrapText(true);

                this.rightPane.getChildren().setAll(rightPaneButtonBox, summaryLabel);
                // Give the text display more space than calendar
                rightPane.setMinWidth(600);
                rightPane.setMaxWidth(600);
                rightPane.setPrefWidth(600);
            }
        }
    }

    /**
     * Read the contents of a file and return the string.
     * @param file A file to read
     * @return The contents of the file as a String
     * @throws IOException If the file cannot be found and opened
     */
    public String readFile(File file) throws IOException{
        return Files.readString(Path.of(file.getAbsolutePath()));
    }

    /**
     * Update the ListView of the projects in the left pane.
     */
    private void updateProjectsListView() {
        recentProjects.getProjectsListView().getItems().setAll(projects);
    }

    /**
     * Get the file extension from a file name
     * @param fileName The name of a file (Can be full path as well)
     * @return The file extension as a string (not including period)
     */
    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    /**
     * Open a FileChooser to select a file from the computers directories.
     * @return File wrapped in Optional
     */
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

    /**
     * Select a project and update displayed content.
     * @param project The project to select
     */
    private void projectSelection(Project project) {
        String projectName = project != null ? project.getName() : "No Project Selected";
        this.selectedProjectText.setText(projectName);
        this.selectedProject = project;
        this.fileActionsBox.hasSelectedProjectProperty().set(project != null);
        createFilesPane(); //Update the files display
    }

    /**
     * Select a file and update displayed content.
     * @param file
     */
    private void setSelectedFile(File file) {
        String fileName = file != null ? file.getName() : "No File Selected";
        this.selectedFile = file;
        fileActionsBox.hasSelectedFilesProperty().set(file != null);
        selectedFileText.setText(fileName);
    }

    /**
     * Prompt the user to add a file from their computer's storage.
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
                        addFile(file);
                        updateConfig(selectedProject, selectedProject.getDirectory());
                    }
                }
        );
    }

    /**
     * Add a given file to the current selectedProject.
     * @param file
     */
    public void addFile(File file) {
        selectedProject.addInputFile(file);
        filesPane.getChildren().add(createFileButton(file));
    }

    /**
     * Remove the current selected file from its project.
     */
    private void removeFile() {
        if (selectedProject == null) {
            return;
        }
        if (selectedFile != null) {
            logger.info("Removing file from project: {}", selectedFile.getName());
            selectedProject.removeFile(selectedFile.getName());
            updateConfig(selectedProject, selectedProject.getDirectory());
            filesPane.getChildren().removeIf(f -> (f.getId().equals(selectedFile.getName())));
            setSelectedFile(null);
            selectedFileText.setText("No File Selected");
        }
    }

    /**
     * Create a new deadline in the calendar
     */
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

    /**
     * Create a new export csv file from the currently selected PDF file
     */
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Name");
            alert.setHeaderText(null);

            result.ifPresent(exportFileName -> {
                if (exportFileName.equals("")) {
                    alert.setContentText("Cannot be empty name");
                    alert.showAndWait();
                } else if (containsIllegalCharacters(exportFileName)) {
                    alert.setContentText("Illegal file name characters");
                    alert.showAndWait();
                } else if (selectedProject.getAllFileNames().contains(selectedProject.getDirectory() + "\\" + exportFileName + ".csv")) {
                    alert.setContentText("A file with that name is already in the project.");
                    alert.showAndWait();
                } else if (!exportFileName.trim().isEmpty()) {
                    // Create the file
                    Path exportPath = Paths.get(selectedProject.getDirectory().getPath(), exportFileName + ".csv");
                    File exportFile = exportPath.toFile();
                    selectedProject.addSummaryFile(exportFile);
                    filesPane.getChildren().add(createFileButton(exportFile));
                    // Actually populate the file with generated data
                    PopulateCsv.GenerateCsv(selectedFile.getAbsolutePath(), exportPath.toString());
                    updateConfig(selectedProject, selectedProject.getDirectory());
                }
            });
        }
    }

    /**
     * Create a new summary txt file from the currently selected PDF file.
     */
    private void summarize() {
        if (selectedProject == null) {
            return;
        }
        if (selectedFile != null && getFileExtension(selectedFile.getName()).equals("pdf")) {
            TextInputDialog summarizeDialog = new TextInputDialog();
            summarizeDialog.setTitle("Summarize");
            summarizeDialog.setHeaderText("Output file");
            summarizeDialog.setContentText("Enter the name of the output file:");

            Optional<String> result = summarizeDialog.showAndWait();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Name");
            alert.setHeaderText(null);
            result.ifPresent(summarizeFileName -> {
                if (summarizeFileName.equals("")) {
                    alert.setContentText("Cannot be empty name");
                    alert.showAndWait();
                } else if (containsIllegalCharacters(summarizeFileName)) {
                    alert.setContentText("Illegal file name characters");
                    alert.showAndWait();
                } else if (selectedProject.getAllFileNames().contains(selectedProject.getDirectory() + "\\" + summarizeFileName + ".txt")) {
                    alert.setContentText("A file with that name is already in the project.");
                    alert.showAndWait();
                } else if (!summarizeFileName.trim().isEmpty()) {
                    // Create the txt file
                    Path summaryPath = Paths.get(selectedProject.getDirectory().getPath(), summarizeFileName + ".txt");
                    File summaryFile = summaryPath.toFile();
                    selectedProject.addSummaryFile(summaryFile);
                    filesPane.getChildren().add(createFileButton(summaryFile));
                    // Populate the txt with generated data
                    PopulateTxt.GenerateTxt(selectedFile.getAbsolutePath(), summaryPath.toString());
                    updateConfig(selectedProject, selectedProject.getDirectory());
                }
            });
        }
    }

    /**
     * Returns true if the string contains characters that are illegal for Windows paths and file names
     * @param pathName The name of a Windows path
     * @return illegal characters boolean
     */
    private boolean containsIllegalCharacters(String pathName) {
        return pathName.matches(".*[<>:\"/\\|?*].*");
    }

    /**
     * Save the project information to the programs config toml
     * @param project A project to save
     * @param folder The location of the projects toml file
     */
    public void updateConfig(Project project, File folder) {
        try {
            ProjectManager.saveProject(project, folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true if the given project name is in the list of projects
     * @param pName The name of a project
     * @return does the project exist boolean
     */
    public boolean containsProject(String pName) {
        for (Project p : projects) {
            if (p.getName().equals(pName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove every project from the list of projects
     */
    public void clearAllProjects() {
        projects.clear();
        projectSelection(null);
        updateProjectsListView();
    }

    /**
     * Get the current selectedProject
     * @return selectedProject
     */
    public Project getSelectedProject() {
        return selectedProject;
    }

    /**
     * Get the ProjectsListView
     * @return ListView of projects
     */
    public ListView<Project> getRecentListView() {
        return recentProjects.getProjectsListView();
    }

    /**
     * Get the text label of summary information
     * @return summaryLabel
     */
    public Label getRightPaneLabel() {
        return summaryLabel;
    }

    /**
     * Get the number of projects
     * @return number of projects
     */
    public int getProjectCount() {
        return projects.size();
    }

}

