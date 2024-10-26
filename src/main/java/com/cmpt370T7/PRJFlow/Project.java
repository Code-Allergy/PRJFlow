package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private String name;
    private List<File> files;

    public Project(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    public List<File> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    public void addFile(File file) {
        files.add(file);
    }

    @Override
    public String toString() {
        return name;
    }
}
