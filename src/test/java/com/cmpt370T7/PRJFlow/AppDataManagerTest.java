package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    }

    @AfterEach
    void tearDown() {
        if (tempDirFile.exists()) {
            tempDirFile.delete();
        }
        AppDataManager.resetInstance();
    }

    @Test
    void test_get_database_file() throws IOException {
        // When
        AppDataManager.instantiateAt(tempDirFile);
        appDataManager = AppDataManager.getInstance();
        File databaseFile = appDataManager.getDatabaseFile();

        // Then
        assertThat(databaseFile).isEqualTo(new File(String.valueOf(tempDir), DATABASE_FILE));
    }

    @Test
    void test_get_config_file() throws IOException {
        // When
        AppDataManager.instantiateAt(tempDirFile);
        appDataManager = AppDataManager.getInstance();
        File configFile = appDataManager.getConfigFile();

        // Then
        assertThat(configFile).isEqualTo(new File(String.valueOf(tempDir), CONFIG_FILE));
    }

    @Test
    void should_instantiate_AppDataManager() throws IOException {
        AppDataManager.instantiate();
        AppDataManager appDataManager = AppDataManager.getInstance();
        assertThat(appDataManager).isNotNull();
    }

    @Test
    void should_throw_exception_when_getInstance_called_without_instantiate() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(AppDataManager::getInstance)
                .withMessageContaining("not instantiated");
    }

    @Test
    void should_create_ConfigManager_and_GlobalTermsDatabase_on_instantiate() throws IOException {
        AppDataManager.instantiate();
        AppDataManager appDataManager = AppDataManager.getInstance();

        assertThat(appDataManager.getConfigManager()).isNotNull();
    }
}
