package com.cmpt370T7.PRJFlow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The AppDataManager class is responsible for managing application data, including
 * configuration files and Tesseract OCR data. It handles the creation and
 * initialization of directories and files necessary for the application to function
 * correctly. This class follows the singleton design pattern to ensure that only one
 * instance of AppDataManager is created throughout the application lifecycle.
 */
public class AppDataManager {
    /// The folder within the system appdata folder to store the configuration
    private static final String APPDATA_FOLDER = "PRJFlow";

    private static Logger logger;

    /// Singleton instance of AppDataManager
    private static AppDataManager instance;
    private final ConfigManager configManager;

    private final File appDataDirectory;
    private final File tesseractDataDirectory;

    /**
     * Default constructor, initializes the AppDataManager with the default app name of PRJFlow.
     */
    private AppDataManager() throws IOException {
        this(APPDATA_FOLDER);
    }

    /**
     * Overloaded constructor, initializes the AppDataManager with a specified application name
     * for the directory.
     *
     * @param appName the name of the application, used to create a directory in the app data path.
     */
    private AppDataManager(String appName) throws IOException {
        logger = LoggerFactory.getLogger(AppDataManager.class);
        appDataDirectory = getAppDataDirectory(appName);
        createDirectoryIfNotExists(appDataDirectory);
        this.tesseractDataDirectory = new File(appDataDirectory, "tesseract_data");
        this.configManager = new ConfigManager(getConfigFile());
        initializeTesseractData();
    }

    /**
     * Overloaded constructor for testing, initializes the AppDataManager with a custom directory.
     *
     * @param customDirectory the directory to use for application data storage.
     */
    private AppDataManager(File customDirectory) throws IOException {
        this.appDataDirectory = customDirectory;
        createDirectoryIfNotExists(appDataDirectory);
        this.configManager = new ConfigManager(getConfigFile());
        this.tesseractDataDirectory = new File(appDataDirectory, "tesseract_data");
        initializeTesseractData();
    }

    /**
     * Instantiates the default singleton instance of AppDataManager.
     * If an instance already exists, a warning is logged.
     */
    public static void instantiate() throws IOException {
        if (instance == null) {
            instance = new AppDataManager();
        } else {
            logger.warn("Ignored duplicate instantiate of AppDataManager.");
        }
    }

    /**
     * Instantiates the AppDataManager singleton at a specified path.
     * If an instance already exists, a warning is logged.
     *
     * @param customDirectory the directory to use for application data storage.
     */
    public static void instantiateAt(File customDirectory) throws IOException {
        if (instance == null) {
            instance = new AppDataManager(customDirectory);
        } else {
            logger.warn("Ignored duplicate instantiateAt of AppDataManager.");
        }
    }

    /**
     * Returns the singleton instance of AppDataManager.
     *
     * @return the instance of AppDataManager.
     * @throws IllegalStateException if the instance has not been instantiated.
     */
    public static AppDataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "AppDataManager not instantiated. Call instantiate() first."
            );
        }
        return instance;
    }

    /**
     * Returns the directory of the system's app data path for the specified application name.
     *
     * @param appName the name of the application for which the app data directory is to be retrieved.
     * @return a File object representing the app data directory.
     */
    private File getAppDataDirectory(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        String appDataPath;

        if (os.contains("win")) {
            appDataPath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            appDataPath = System.getProperty("user.home") +
            "/Library/Application Support";
        } else {
            appDataPath = System.getenv("XDG_CONFIG_HOME");
            if (appDataPath == null || appDataPath.isEmpty()) {
                appDataPath = System.getProperty("user.home") + "/.config";
            }
        }

        return new File(appDataPath, appName);
    }

    /**
     * Copies the Tesseract data language files to the user app data folder.
     */
    private void initializeTesseractData() {
        Path tessDataPath;
        try {
            try {
                URI tessDataURL = Objects.requireNonNull(AppDataManager.class.getResource("tessdata")).toURI();
                String pathStr = Paths.get(tessDataURL).toString();
                tessDataPath = Paths.get(pathStr);
            } catch (NullPointerException e) {
                logger.error("Failed to load tessdata from resources!");
                logger.error(e.getMessage());
                throw e;
            }

            createDirectoryIfNotExists(tesseractDataDirectory);

            // Iterate through each file in the tessdata directory
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(tessDataPath)) {
                for (Path sourceFile : directoryStream) {
                    Path destFile = tesseractDataDirectory.toPath().resolve(sourceFile.getFileName().toString());

                    // Only copy if the file doesn't exist or it's newer
                    if (!Files.exists(destFile) || Files.getLastModifiedTime(sourceFile).toMillis() >
                            Files.getLastModifiedTime(destFile).toMillis()) {
                        try (InputStream is = Files.newInputStream(sourceFile)) {
                            Files.copy(is, destFile, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Tesseract data file could not be loaded. Exiting.");
            System.exit(1);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a directory if it does not already exist.
     *
     * @param dir the directory to create.
     */
    private void createDirectoryIfNotExists(File dir) throws IOException {
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                logger.info("Directory created: {}", dir.getAbsolutePath());
            } else {
                logger.error(
                    "Failed to create critical directory: {}",
                    dir.getAbsolutePath()
                );
                throw new IOException();
            }
        }
    }

    /**
     * Resets the singleton instance for testing purposes.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Returns the file handle of the SQLite database where global terms are stored.
     *
     * @return a File object representing the global terms database file.
     */
    public File getDatabaseFile() {
        return new File(appDataDirectory, "global_terms.db");
    }

    /**
     * Returns the file handle of the configuration file.
     *
     * @return a File object representing the configuration file.
     */
    public File getConfigFile() {
        return new File(appDataDirectory, "config.toml");
    }

    /**
     * Returns the file handle of the Tesseract data directory.
     *
     * @return a File object representing the Tesseract data directory.
     */
    public File getTesseractDataDirectory() {
        return tesseractDataDirectory;
    }

    /**
     * Gets the global instance of {@link ConfigManager}.
     *
     * @return the instance of ConfigManager associated with the global AppDataManager.
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
