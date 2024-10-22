package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

// TODO we should also verify errors

public class AppDataManagerTest {
    private static final String DATABASE_FILE = "global_terms.db";
    private static final String CONFIG_FILE = "config.toml";

    @TempDir
    private Path tempDir;
    private File tempDirFile;
    private AppDataManager appDataManager;

    @BeforeEach
    void setUp() {
        tempDirFile = tempDir.toFile();
        appDataManager = new AppDataManager(tempDirFile);
    }

    @AfterEach
    void tearDown() {
        if (tempDirFile.exists()) {
            tempDirFile.delete();
        }
    }

    @Test
    void test_get_database_file() {
        // When
        File databaseFile = appDataManager.getDatabaseFile();

        // Then
        assertThat(databaseFile).isEqualTo(new File(String.valueOf(tempDir), DATABASE_FILE));
    }

    @Test
    void test_get_config_file() {
        // When
        File configFile = appDataManager.getConfigFile();

        // Then
        assertThat(configFile).isEqualTo(new File(String.valueOf(tempDir), CONFIG_FILE));
    }
}
