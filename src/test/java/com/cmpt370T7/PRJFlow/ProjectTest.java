package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectTest {
    private Project project;
    private File directory;

    @BeforeEach
    public void setUp() {
        directory = new File("test-directory");
        project = new Project("Test Project", directory);
    }

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
}