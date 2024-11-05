package com.cmpt370T7.PRJFlow;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private final File configFile;
    private Map<String, Object> configData;

    public ConfigManager(File configFile) {
        this.configFile = configFile;
        loadConfig();
    }

    private void loadConfig() {
        if (configFile.exists()) {
            configData = new Toml().read(configFile).toMap();
        } else {
            configData = new HashMap<>();
        }
    }

    public void setConfigValue(String key, Object value) {
        configData.put(key, value);
    }

    public Object getConfigValue(String key) {
        return configData.get(key);
    }

    public void saveConfig() throws IOException {
        TomlWriter writer = new TomlWriter();
        writer.write(configData, configFile);
    }


    public Map<LocalDate, List<String>> getReminderMap() {
        Map<LocalDate, List<String>> reminderMap = new HashMap<>();
        Object reminderDataObj = configData.get("reminders");

        if (reminderDataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> reminderData = (Map<String, List<String>>) reminderDataObj;
            for (Map.Entry<String, List<String>> entry : reminderData.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey(), DateTimeFormatter.ISO_LOCAL_DATE);
                reminderMap.put(date, entry.getValue());
            }
        } else {
            logger.warn("Reminders data not found or not in the expected format.");
        }

        return reminderMap;
    }

    public void setReminderMap(Map<LocalDate, List<String>> reminderMap) {
        Map<String, List<String>> reminderData = new HashMap<>();
        for (Map.Entry<LocalDate, List<String>> entry : reminderMap.entrySet()) {
            reminderData.put(entry.getKey().format(DateTimeFormatter.ISO_LOCAL_DATE), entry.getValue());
        }
        configData.put("reminders", reminderData);
    }

    @SuppressWarnings("unchecked")
    public List<Project> getRecentProjects() {
        List<Project> projectList = new ArrayList<>();
        Object recentProjectsObj = configData.get("recent_projects");

        if (recentProjectsObj instanceof List) {
            List<String> projects = (List<String>) recentProjectsObj;
            for (String project : projects) {
                try {
                    projectList.add(ProjectManager.openProject(new File(project, "prjflowconfig.toml")));
                } catch (NoSuchFieldException | FileNotFoundException e) {
                    logger.warn("Could not find project {}", project);
                }
            }
        } else {
            logger.warn("Recent projects data not found or not in the expected format.");
        }

        return projectList;
    }

    public void setRecentProjects(List<Project> projects) {
        List<String> projectPaths = new ArrayList<>();
        for (Project project : projects) {
            projectPaths.add(project.getDirectory().toString());
        }
        configData.put("recent_projects", projectPaths);
    }
}

