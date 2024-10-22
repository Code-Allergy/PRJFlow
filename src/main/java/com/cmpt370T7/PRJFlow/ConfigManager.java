package com.cmpt370T7.PRJFlow;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
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
}

