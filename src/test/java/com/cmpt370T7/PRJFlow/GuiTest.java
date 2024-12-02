package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.gui.GUI;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.assertThat;

public class GuiTest extends ApplicationTest {
    private GUI gui;
    private String testProjectName;
    private String defaultDirectoryString;
    private File testFile;
    private Project testProject;

    /* Testing GUI's is more difficult than I thought,
       Sometimes the tests fail, but repeated runnings let them pass.
       I think this is an issue with how Javafx needs to be run on certain thread and ApplicationTest causes issues.
     */


    @BeforeEach
    public void setUp() {
        Platform.runLater(() -> gui.clearAllProjects());
        //Platform.runLater(() -> gui = new GUI());
    }

    public String readFile(File file) throws IOException {
        return Files.readString(Path.of(file.getAbsolutePath()));
    }



    @Override
    public void start(Stage stage) throws Exception {
        AppDataManager.instantiate();
        stage.setTitle("GuiTest");
        gui = new GUI();
        gui.clearAllProjects();
        //Scene scene = new Scene(gui);
        testProjectName = "Test Project one";
        defaultDirectoryString = System.getProperty("user.home") + "\\Documents";
        testFile = new File(System.getProperty("user.dir") + "\\src\\test\\java\\com\\cmpt370T7\\PRJFlow\\testFile.txt");
        testProject = new Project(testProjectName, new File(defaultDirectoryString));
        System.out.println(testFile.toString());
        Scene scene = new Scene(gui);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        stage.requestFocus();
    }

    // User Story: Create New Project

    @Test
    public void create_new_project() {
        clickOn(hasText("New Project"));
        write(testProjectName);
        clickOn(hasText("OK"));
        press(KeyCode.ENTER);
        assertThat(gui.containsProject(testProjectName)).isTrue();
    }

    @Test
    public void create_new_project_empty_name() {
        assertThat(gui.getProjectCount()).isEqualTo(0);
        clickOn(hasText("New Project"));
        write("");
        clickOn(hasText("OK"));
        clickOn(hasText("OK")); //Warning message
        assertThat(gui.getProjectCount()).isEqualTo(0);
    }

    @Test
    public void create_project_duplicate_name() {
        gui.addProject(testProject);
        assertThat(gui.getProjectCount()).isEqualTo(1);
        clickOn(hasText("New Project"));
        write(testProjectName);
        clickOn(hasText("OK")); //Warning message
        assertThat(gui.getProjectCount()).isEqualTo(1);
    }

    @Test
    public void cancel_create_new_project() {
        assertThat(gui.getProjectCount()).isEqualTo(0);
        clickOn(hasText("New Project"));
        clickOn(hasText("Cancel"));
        assertThat(gui.getProjectCount()).isEqualTo(0);
    }

    @Test
    public void cancel_after_naming_project() {
        clickOn(hasText("New Project"));
        write(testProjectName);
        clickOn(hasText("OK"));
        press(KeyCode.ESCAPE);
        assertThat(gui.getProjectCount()).isEqualTo(0);
    }



    //User Story: View and edit project info

    @Test
    public void delete_project() {
        gui.addProject(testProject);

        clickOn(gui.getRecentListView());
        press(KeyCode.ENTER); //First and only item in ListView
        clickOn(hasText("Delete Project"));
        clickOn(hasText("OK"));
        assertThat(gui.containsProject(testProjectName)).isFalse();
    }

    @Test
    public void cancel_delete_project() {
        gui.addProject(testProject);

        clickOn(gui.getRecentListView());
        press(KeyCode.ENTER); //First and only item in ListView
        clickOn(hasText("Delete Project"));
        clickOn(hasText("Cancel"));
        assertThat(gui.containsProject(testProjectName)).isTrue();
    }

    @Test
    public void add_file() {
        gui.addProject(testProject);
        clickOn(gui.getRecentListView());
        press(KeyCode.ENTER); //First and only item in ListView

        clickOn(hasText("Add File"));

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(testFile.getAbsolutePath());
        c.setContents(stringSelection, stringSelection);
        press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);
        push(KeyCode.ENTER);
        push(KeyCode.ENTER);

        assertThat(gui.getSelectedProject().contains(testFile)).isTrue();
    }

    @Test
    public void delete_file() {
        gui.addProject(testProject);
        Platform.runLater(() -> gui.addFile(testFile));
        sleep(1);
        clickOn(gui.getRecentListView());
        press(KeyCode.ENTER); //First and only item in ListView
        clickOn(hasText(testFile.getName()));
        clickOn("Remove File");

        assertThat(gui.getSelectedProject().contains(testFile)).isFalse();
    }

    @Test
    public void view_file_summary() {
        gui.addProject(testProject);
        Platform.runLater(() -> gui.addFile(testFile));
        String fileContents = null;
        try {
            fileContents = readFile(testFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
        sleep(1);


        doubleClickOn(hasText(testFile.getName()));

        assertThat(gui.getRightPaneLabel().getText()).isEqualTo(fileContents);
    }

}
