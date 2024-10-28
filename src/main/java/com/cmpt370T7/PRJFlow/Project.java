package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void removeFile(String fileName) {
        files = files.stream().filter(f -> !f.getName().equals(fileName)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name;
    }
}
