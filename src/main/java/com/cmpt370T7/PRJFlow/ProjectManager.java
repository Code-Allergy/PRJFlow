package com.cmpt370T7.PRJFlow;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class ProjectManager {

    /**
     * Opens a previously saved PRJFlow project
     *
     * @param tomlFile Config file for the project to open. Should be named prjflowconfig.toml
     * @return A new project object, specified by the config file
     * @throws NoSuchFieldException when the required field 'PRJFlowTitle' is not in the config file
     */
    static Project openProject(File tomlFile) throws NoSuchFieldException{

        Toml config = new Toml().read(tomlFile);
        Project open = null;

        //Make sure config file has a title, and create a new project file. otherwise, throw an exception
        if (config.contains("PRJFlowTitle")){
            open = new Project(config.getString("PRJFlowTitle"), tomlFile.getParentFile());
        } else
            throw new NoSuchFieldException("TOML Config File " + tomlFile.getName() + " missing title field.");

        //Read owner field
        if (config.contains("owner"))
            open.setOwner(config.getString("owner"));

        //Populate the list of input files from the config TOML
        if (config.containsTableArray("inputFiles")){
            List<String> ConfigInputList = config.getList("inputFiles");
            for (String nextInputFile : ConfigInputList) {
                if (nextInputFile == null) break;
                File addFile = new File(nextInputFile);

                if (addFile.exists())
                    try {
                        open.addInputFile(addFile);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                else
                    System.err.println(addFile.getPath() + " input file specified in the project config does not exist.");
            }
        }

        //Populate the list of generated summary files from the config TOML
        if (config.containsTableArray("summaryFiles")){
            List<String> ConfigSummaryList = config.getList("summaryFiles");
            for (String NextSummaryFile : ConfigSummaryList) {
                if (NextSummaryFile == null) break;
                File addFile = new File(NextSummaryFile);

                if (addFile.exists())
                    open.addSummaryFile(addFile);
                else
                    System.err.println(addFile.getPath() + " summary file specified in the project config does not exist.");
            }
        }

        return open;
    }

    /**
     * Saves a Project object as a config file.
     *
     * @param save Project object to save to file
     * @param pathname Path of the config file to save. Can be in any directory, but must be named prjflowconfig.toml
     * @throws IllegalArgumentException when supplied with a null project or an incorrect filename
     * @throws IOException if the prjflowconfig.toml file exists but is not writable
     */
    static void saveProject(Project save, String pathname) throws IllegalArgumentException, IOException {
        if (save == null) throw new IllegalArgumentException("Cannot save null project.");
        File saveFile = new File(pathname);

        if (!saveFile.getName().equals("prjflowconfig.toml")){
            throw new IllegalArgumentException("Config file name must be \"prjflowconfig.toml\"");
        }

        if (!saveFile.exists()){
            saveFile.createNewFile();
        }

        if (!saveFile.canWrite()){
            throw new IOException("Save file is not writable");
        }

        Toml config = new Toml().read(saveFile);
        TomlWriter writer = new TomlWriter();
        Map<String, Object> configMap = config.toMap();

        ArrayList<String> inputFiles = new ArrayList<>();
        for (File file : save.getInputFiles()) {
            inputFiles.add(file.getPath());
        }

        ArrayList<String> summaryFiles = new ArrayList<>();
        for (File file : save.getSummaryFiles()) {
            summaryFiles.add(file.getPath());
        }

        //Create map with all info to write
        configMap.put("PRJFlowTitle", save.getName());
        configMap.put("owner", save.getOwner());
        configMap.put("inputFiles", inputFiles);
        configMap.put("summaryFiles", summaryFiles);

        //Write the ma to the config file
        writer.write(configMap, saveFile);

    }

    public static void main(String[] args) throws IOException {

        //File settings = new File("test.tomlaaa");

        File testFile = new File("test-project/prjflowconfig.toml");

        Project T = null;

        try {
            T = openProject(testFile);
        } catch (NoSuchFieldException e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

        assert T != null;
//        T.addInputFile(new File("test-project/CMPT332-2024.fall.syllabus.pdf"));
//        T.addInputFile(new File("test-project/CMPT370F24_Syllabus.pdf"));
//        T.addSummaryFile(new File("test-project/dummy_output.csv"));

        for (File file : T.getSummaryFiles()) {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write("1, 2, 3, testing3");
            myWriter.close();
        }

        saveProject(T, "test-project/prjflowconfig.toml");
    }
}
