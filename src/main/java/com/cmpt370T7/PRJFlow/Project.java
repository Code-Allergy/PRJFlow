package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;

public class Project {

    private String name;
    private ArrayList<File> files;

    Project(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    ArrayList<File> getFiles() {
        return files;
    }

    String getName() {
        return name;
    }

    void addFile(String fileName) {
        files.add(new File(fileName));
    }


}
