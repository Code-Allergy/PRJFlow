package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

class ConfigManagerTest {
    @TempDir
    Path tempDir;
    private ConfigManager configManager;
    private File configFile;

    @BeforeEach
    void setUp() {
        configFile = tempDir.resolve("config.toml").toFile();
    }

    @Test
    void constructor_should_create_new_config_when_file_does_not_exist() {
        // When
        configManager = new ConfigManager(configFile);

        // Then
        assertThat(configFile).doesNotExist();
        assertThat(configManager.getConfigValue("nonexistent")).isNull();
    }

    @Test
    void constructor_should_load_existing_config_when_file_exists() throws IOException {
        // Given
        Files.writeString(configFile.toPath(), "test_key = \"test_value\"");

        // When
        configManager = new ConfigManager(configFile);

        // Then
        assertThat(configManager.getConfigValue("test_key")).isEqualTo("test_value");
    }

    @Test
    void setConfigValue_should_store_string_value() {
        // Given
        configManager = new ConfigManager(configFile);

        // When
        configManager.setConfigValue("test_key", "test_value");

        // Then
        assertThat(configManager.getConfigValue("test_key")).isEqualTo("test_value");
    }

    @Test
    void setConfigValue_should_store_numeric_value() {
        // Given
        configManager = new ConfigManager(configFile);

        // When
        configManager.setConfigValue("number_key", 42);

        // Then
        assertThat(configManager.getConfigValue("number_key")).isEqualTo(42);
    }

    @Test
    void setConfigValue_should_store_boolean_value() {
        // Given
        configManager = new ConfigManager(configFile);

        // When
        configManager.setConfigValue("bool_key", true);

        // Then
        assertThat(configManager.getConfigValue("bool_key")).isEqualTo(true);
    }

    @Test
    void getConfigValue_should_return_null_for_nonexistent_key() {
        // Given
        configManager = new ConfigManager(configFile);

        // When
        Object value = configManager.getConfigValue("nonexistent_key");

        // Then
        assertThat(value).isNull();
    }

    @Test
    void saveConfig_should_create_file_when_it_does_not_exist() throws IOException {
        // Given
        configManager = new ConfigManager(configFile);
        configManager.setConfigValue("test_key", "test_value");

        // When
        configManager.saveConfig();

        // Then
        assertThat(configFile).exists();
        String content = Files.readString(configFile.toPath());
        assertThat(content)
                .contains("test_key")
                .contains("test_value");
    }

    @Test
    void saveConfig_should_update_existing_file() throws IOException {
        // Given
        Files.writeString(configFile.toPath(), "existing_key = \"existing_value\"");
        configManager = new ConfigManager(configFile);
        configManager.setConfigValue("new_key", "new_value");

        // When
        configManager.saveConfig();

        // Then
        String content = Files.readString(configFile.toPath());
        assertThat(content)
                .contains("new_key")
                .contains("new_value");
    }

    @Test
    void saveConfig_should_handle_complex_data_types() throws IOException {
        // Given
        configManager = new ConfigManager(configFile);
        Map<String, Object> complexData = Map.of(
                "nested_key", "nested_value",
                "number", 42,
                "boolean", true
        );
        configManager.setConfigValue("complex", complexData);

        // When
        configManager.saveConfig();

        // Then
        assertThat(configFile).exists();
        String content = Files.readString(configFile.toPath());
        assertThat(content)
                .contains("complex")
                .contains("nested_key");
    }

    // TODO
//    @Test
//    void saveConfig_should_throw_IOException_when_file_is_not_writable() {
//        // Given
//        configManager = new ConfigManager(configFile);
//        try {
//            Files.setPosixFilePermissions(configFile.toPath(),
//                    PosixFilePermissions.fromString("r--r--r--"));
//        } catch (UnsupportedOperationException e) {
//            // If POSIX is not supported, try the basic method (windows)
//            if (!configFile.setReadOnly()) {
//                throw new TestInstantiationException("Cannot make file read-only on this system");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // When/Then
//        assertThatThrownBy(() -> configManager.saveConfig())
//                .isInstanceOf(IOException.class);
//    }

    @AfterEach
    void tearDown() {
        if (configFile.exists()) {
            configFile.delete();
        }
    }
}