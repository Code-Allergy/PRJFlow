package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Project {

    private String name;
    private List<File> files;

    Project(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    List<File> getFiles() {
        return files;
    }

    String getName() {
        return name;
    }

    void addFile(File file) {
        files.add(file);
    }

    void removeFile(String fileName) {
        files = files.stream().filter(f -> !f.getName().equals(fileName)).collect(Collectors.toList());
    }


}
