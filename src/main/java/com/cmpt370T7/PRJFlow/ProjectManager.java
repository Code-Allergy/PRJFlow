package com.cmpt370T7.PRJFlow;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class ProjectManager {
    private static final Logger logger = LoggerFactory.getLogger(ProjectManager.class);


    /**
     * Opens a previously saved PRJFlow project
     *
     * @param tomlFile Config file for the project to open. Should be named prjflowconfig.toml
     * @return A new project object, specified by the config file
     * @throws NoSuchFieldException when the required field 'PRJFlowTitle' is not in the config file
     */
    static Project openProject(File tomlFile) throws NoSuchFieldException, FileNotFoundException {
        Toml config = new Toml().read(tomlFile);
        Project open;

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
                    open.addInputFile(addFile);

                else
                    logger.warn("Input file specified in the project config does not exist: {}", addFile.getPath());
            }
        }

        //Populate the list of generated summary files from the config TOML
        if (config.containsTableArray("summaryFiles")){
            List<String> ConfigSummaryList = config.getList("summaryFiles");
            for (String nextSummaryFile : ConfigSummaryList) {
                if (nextSummaryFile == null) break;
                File addFile = new File(nextSummaryFile);

                if (addFile.exists())
                    open.addSummaryFile(addFile);
                else
                    logger.warn("Summary file specified in the project config does not exist: {}", addFile.getPath());
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
    public static void saveProject(Project save, File pathname) throws IllegalArgumentException, IOException {
        if (save == null) throw new IllegalArgumentException("Cannot save null project.");
        File saveFile = getConfigFile(pathname);

        Toml config = new Toml().read(saveFile);
        TomlWriter writer = new TomlWriter();
        Map<String, Object> configMap = config.toMap();

        ArrayList<String> inputFiles = new ArrayList<>();
        for (File file : save.getInputFiles()) {
            inputFiles.add(file.getPath().replace("\\", "/"));
        }

        ArrayList<String> summaryFiles = new ArrayList<>();
        for (File file : save.getSummaryFiles()) {
            summaryFiles.add(file.getPath().replace("\\", "/"));
        }

        //Create map with all info to write
        configMap.put("PRJFlowTitle", save.getName());
        configMap.put("owner", save.getOwner());
        configMap.put("inputFiles", inputFiles);
        configMap.put("summaryFiles", summaryFiles);

        //Write the ma to the config file
        writer.write(configMap, saveFile);

    }

    protected static File getConfigFile(File pathname) throws IOException {
        File saveFile = new File(pathname, "prjflowconfig.toml");

        if (!saveFile.getName().equals("prjflowconfig.toml")){
            throw new IllegalArgumentException("Config file name must be \"prjflowconfig.toml\"");
        }

        if (!saveFile.exists() && !saveFile.createNewFile()){
            throw new IOException("Could not create config file " + saveFile.getPath());
        }

        if (!saveFile.canWrite()){
            throw new IOException("Save file is not writable");
        }
        return saveFile;
    }

    // TODO real testing of this class
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
    }
}
