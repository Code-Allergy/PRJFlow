package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Project {

    private String name;
    private File directory;
    private List<File> files;

    public Project(String name, File directory) {
        this.name = name;
        this.directory = directory;
        this.files = new ArrayList<>();
        addInitialFiles();
    }

    public List<File> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    public File getDirectory() {
        return directory;
    }

    public void addFile(File file) {
        files.add(file);
    }

    public void removeFile(String fileName) {
        files = files.stream().filter(f -> !f.getName().equals(fileName)).collect(Collectors.toList());
    }

    private void addInitialFiles() {
        File[] directoryFiles = directory.listFiles();
        for (File f : directoryFiles) {
            files.add(f);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
