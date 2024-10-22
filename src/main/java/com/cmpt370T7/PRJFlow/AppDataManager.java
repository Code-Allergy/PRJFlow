package com.cmpt370T7.PRJFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AppDataManager {
    /// The folder within the system appdata folder to store the configuration
    private static final String APPDATA_FOLDER = "PRJFlow";

    /// Singleton instance of AppDataManager
    private static AppDataManager instance;

    private static Logger logger;
    private final File appDataDirectory;

    private final ConfigManager configManager;
    private final GlobalTermsDatabase globalTermsDatabase;

    /// Default constructor, uses default name of PRJFlow.
    private AppDataManager() {
        this(APPDATA_FOLDER);
    }

    /// Overloaded constructor, uses passed in app name for directory.
    private AppDataManager(String appName) {
        logger = LoggerFactory.getLogger(AppDataManager.class);
        appDataDirectory = getAppDataDirectory(appName);
        createDirectoryIfNotExists(appDataDirectory);
        configManager = new ConfigManager(getConfigFile());
        globalTermsDatabase = new GlobalTermsDatabase(getDatabaseFile());
    }

    /// Overloaded constructor for testing, accepts a custom directory.
    public AppDataManager(File customDirectory) {
        this.appDataDirectory = customDirectory;
        createDirectoryIfNotExists(appDataDirectory);
        configManager = new ConfigManager(getConfigFile());
        globalTermsDatabase = new GlobalTermsDatabase(getDatabaseFile());
    }

    /// Instantiate method to create the default singleton instance
    public static void instantiate() {
        if (instance == null) {
            instance = new AppDataManager();
        } else {
            logger.warn("Ignored duplicate instantiation of AppDataManager.");
        }
    }

    public static AppDataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AppDataManager not instantiated. Call instantiate() first.");
        }
        return instance;
    }

    // Reset method for testing purposes
    public static void resetInstance() {
        instance = null;
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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GlobalTermsDatabase getGlobalTermsDatabase() {
        return globalTermsDatabase;
    }
}
