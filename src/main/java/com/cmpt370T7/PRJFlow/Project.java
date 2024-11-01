package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Project {

    private String name;
    private File directory;
    private String owner;
    private List<File> inputFiles;
    private List<File> summaryFiles;

    public Project(String name, File directory) {
        this.name = name;
        this.directory = directory;
        this.inputFiles = new ArrayList<>();
        this.summaryFiles = new ArrayList<>();
        addInitialFiles();
    }

    private String owner;
    private ArrayList<File> inputFiles;
    private ArrayList<File> summaryFiles;

    public Project(String name) {
        this.name = name;
        this.inputFiles = new ArrayList<>();
        this.summaryFiles = new ArrayList<>();
    }

    ArrayList<String> getInputFileNames() {
        ArrayList<String> s = new ArrayList<>();
        for (File f : inputFiles) {
            s.add(f.toString());
        }

        return s;
    }

    ArrayList<String> getSummaryFileNames() {
        ArrayList<String> s = new ArrayList<>();
        for (File f : summaryFiles) {
            s.add(f.toString());
        }

        return s;
    }

    ArrayList<File> getInputFiles(){
        return inputFiles;
    }

    ArrayList<File> getSummaryFiles(){
        return summaryFiles;
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

    List<File> getInputFiles(){
        return inputFiles;
    }

    List<File> getSummaryFiles(){
        return summaryFiles;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    void addInputFile(File fileName) {
        inputFiles.add(fileName);
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
        return s;
    }

    private void addInitialFiles() {
        File[] directoryFiles = directory.listFiles();
        for (File f : directoryFiles) {
            inputFiles.add(f);
        }
    public void removeInputFile(String fileName) {
        inputFiles = (ArrayList<File>) inputFiles.stream().filter(f -> !f.getName().equals(fileName)).collect(Collectors.toList());
    }

    void removeSummaryFile(File fileName){

    }

    @Override
    public String toString() {
        return name;
    }
}
<<<<<<< HEAD
=======

>>>>>>> origin/frontend-will
