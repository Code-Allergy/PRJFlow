package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.gui.GUI;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiTest extends ApplicationTest {
    private Project project;
    private File directory;
    private GUI gui;
    private String testProjectName;

    @BeforeEach
    public void setUp() {
        //directory = new File("test-directory");
        //project = new Project("Test Project", directory);
        //gui = new GUI();
    }

    @Override
    public void start(Stage stage) throws Exception {
        AppDataManager.instantiate();
        stage.setTitle("GuiTest");
        //gui = new GUI();
        //Scene scene = new Scene(gui);
        gui = new GUI();
        gui.clearAllProjects();
        testProjectName = "Test Project 1";
        Scene scene = new Scene(gui);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        stage.requestFocus();
    }

    @Test
    public void contains_new_project_button() {
        verifyThat(".button", hasText("New Project"));
    }

    @Test
    public void create_new_project() {
        clickOn(hasText("New Project"));
        write(testProjectName);
        clickOn(hasText("OK"));
        press(KeyCode.ENTER);
        assertThat(gui.containsProject(testProjectName)).isTrue();
    }

    @Test
    public void delete_project() {
        //clickOn(hasText(testProjectName));

        Project selectedProject = gui.getSelectedProject();
        clickOn(gui.getRecentListView().get);


        clickOn(hasText("Delete Project"));
        clickOn(hasText("OK"));
        //System.out.println()
        assertThat(gui.containsProject(testProjectName)).isFalse();
    }
/*
    @Test
    public void testGetName() {
        assertThat(project.getName()).isEqualTo("Test Project");
    }

    @Test
    public void testGetOwner() {
        project.setOwner("Owner");
        assertThat(project.getOwner()).isEqualTo("Owner");
    }

    @Test
    public void testGetDirectory() {
        assertThat(project.getDirectory()).isEqualTo(directory);
    }

    @Test
    public void testAddInputFile() {
        File file = new File("input-file.txt");
        project.addInputFile(file);
        assertThat(project.getInputFiles()).contains(file);
    }

    @Test
    public void testContains() {
        File file = new File("input-file.txt");
        project.addInputFile(file);
        assertThat(project.contains(file)).isTrue();
    }

    @Test
    public void testAddSummaryFile() {
        File file = new File("summary-file.txt");
        project.addSummaryFile(file);
        assertThat(project.getSummaryFiles()).contains(file);
    }

    @Test
    public void testRemoveFile() {
        File file = new File("input-file.txt");
        project.addInputFile(file);
        project.removeFile(file.getName());
        assertThat(project.getInputFiles()).doesNotContain(file);
    }

    @Test
    public void testGetInputFileNames() {
        File file = new File("input-file.txt");
        project.addInputFile(file);
        List<String> fileNames = project.getInputFileNames();
        assertThat(fileNames).contains(file.toString());
    }

    @Test
    public void testGetSummaryFileNames() {
        File file = new File("summary-file.txt");
        project.addSummaryFile(file);
        List<String> fileNames = project.getSummaryFileNames();
        assertThat(fileNames).contains(file.toString());
    }

    @Test
    public void testGetFile() {
        File file = new File("input-file.txt");
        project.addInputFile(file);
        assertThat(project.getFile(file.getName())).isEqualTo(file);
    }

    @Test
    public void testToString() {
        assertThat(project.toString()).isEqualTo("Test Project");
    }

 */
}
