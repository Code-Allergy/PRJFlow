package com.cmpt370T7.PRJFlow;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Project {
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

public class ProjectManager {


    Project loadProject(String pathname){

        Project p = new Project("");


        return p;
    }

    void saveProject(Project proj, String pathname){

    }

    public static void main(String[] args) throws IOException {

        File settings = new File("test.toml");

        Toml proj = new Toml().read(settings);
        TomlWriter writer = new TomlWriter();

        Map<String, Object> configData = proj.toMap();

        System.out.println(configData.toString());

        ArrayList<String> files = new ArrayList<>();

        files.add("myfile1.pdf");
        files.add("myfile2.pdf");
        files.add("myfile3.pdf");

        //configData.put("meta.title","testing file");
        //configData.put("meta.owner","mine");
        configData.put("pdfs",files);

        writer.write(configData, settings);

    }
}
