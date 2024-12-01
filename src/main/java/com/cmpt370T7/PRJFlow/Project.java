package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Project {

    private final String name;
    private final File directory;
    private String owner;
    private List<File> inputFiles;
    private List<File> summaryFiles;

    public Project(String name, File directory) {
        this.name = name;
        this.directory = directory;
        this.inputFiles = new ArrayList<>();
        this.summaryFiles = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public File getDirectory() {
        return directory;
    }

    public List<File> getInputFiles(){
        return inputFiles;
    }

    List<File> getSummaryFiles(){
        return summaryFiles;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    // Dont allow duplicate files
    public void addInputFile(File file) {
        inputFiles.add(file);
    }

    // True if inputFiles contains a file with the same name
    public boolean contains(File file) {
        for (File f : inputFiles) {
            if (f.getName().equals(file.getName())) {
                return true;
            }
        }
        return false;
    }

    void addSummaryFile(File fileName){
        summaryFiles.add(fileName);
    }

    public void removeFile(String fileName) {
        inputFiles = inputFiles.stream().filter(f -> !f.getName().equals(fileName)).collect(Collectors.toList());
    }

    public ArrayList<String> getInputFileNames() {
        ArrayList<String> s = new ArrayList<>();
        for (File f : inputFiles) {
            s.add(f.toString());
        }
        return s;
    }

    public ArrayList<String> getSummaryFileNames() {
        ArrayList<String> s = new ArrayList<>();
        for (File f : summaryFiles) {
            s.add(f.toString());
        }
        return summaryFiles.stream().map(File::toString).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getAllFileNames() {
        ArrayList<String> allFileNames = getSummaryFileNames();
        allFileNames.addAll(getInputFileNames());
        return allFileNames;
    }

    public void addInitialFiles() {
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles != null) {
            for (File f : directoryFiles) {
                if (f.isFile()) {
                    inputFiles.add(f);
                }
            }
            //Collections.addAll(inputFiles, directoryFiles);
        }

    }

    public File getFile(String fileName) {
        for (File f : inputFiles) {
            if (f.getName().equals(fileName)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}

