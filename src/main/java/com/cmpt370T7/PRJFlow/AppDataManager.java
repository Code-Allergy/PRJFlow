package com.cmpt370T7.PRJFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AppDataManager {
    private static Logger logger;
    private final File appDataDirectory;

    /// Default constructor, uses default name of PRJFlow.
    public AppDataManager() {
        this("PRJFlow");
    }

    /// Overloaded constructor, uses passed in app name for directory. (testing)
    public AppDataManager(String appName) {
        logger = LoggerFactory.getLogger(AppDataManager.class);
        appDataDirectory = getAppDataDirectory(appName);
        createDirectoryIfNotExists(appDataDirectory);
    }

    // Overloaded constructor for testing, accepts a custom directory. (testing)
    public AppDataManager(File customDirectory) {
        this.appDataDirectory = customDirectory;
        createDirectoryIfNotExists(appDataDirectory);
    }

    /// Returns the directory of the system's appdata path.
    private File getAppDataDirectory(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        String appDataPath;

        if (os.contains("win")) {
            appDataPath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            appDataPath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            appDataPath = System.getenv("XDG_CONFIG_HOME");
            if (appDataPath == null || appDataPath.isEmpty()) {
                appDataPath = System.getProperty("user.home") + "/.config";
            }
        }

        return new File(appDataPath, appName);
    }

    // TODO should error
    private void createDirectoryIfNotExists(File dir) {
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                logger.info("Directory created: {}", dir.getAbsolutePath());
            } else {
                logger.warn("Failed to create directory: {}", dir.getAbsolutePath());
            }
        }
    }

    /// Returns the file handle of the Sqlite database where we store global terms.
    public File getDatabaseFile() {
        return new File(appDataDirectory, "global_terms.db");
    }

    /// Returns the file handle of the config file.
    public File getConfigFile() {
        return new File(appDataDirectory, "config.toml");
    }
}
