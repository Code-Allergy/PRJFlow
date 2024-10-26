package com.cmpt370T7.PRJFlow;

import java.util.ArrayList;

public class Project {

    private String name;
    private ArrayList<String> fileNames;

    Project(String name) {
        this.name = name;
        this.fileNames = new ArrayList<>();
    }

    ArrayList<String> getFileNames() {
        return fileNames;
    }

    String getName() {
        return name;
    }

    void addFile(String fileName) {
        fileNames.add(fileName);
    }


}
