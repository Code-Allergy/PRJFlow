package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Project {

    private String name;
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

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    void addInputFile(File fileName) {
        inputFiles.add(fileName);
    }

    void addSummaryFile(File fileName){
        summaryFiles.add(fileName);
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
