package me.Scyy.PrimeWarps.Config;

import com.google.common.base.Charsets;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ConfigFile {

    protected final Plugin plugin;

    protected YamlConfiguration config;
    protected final File configFile;
    protected final String configFilePath;

    public ConfigFile(Plugin plugin, String configFilePath) {

        // Save the plugin reference
        this.plugin = plugin;

        // Save the messages file
        this.configFile = new File(plugin.getDataFolder(), configFilePath);

        // Check if the file exists
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(configFilePath, false);
        }

        // Create the yml reference
        this.config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        // Save the message file path
        this.configFilePath = configFilePath;

    }

    // Config Manipulation

    public void reloadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            InputStream defIMessagesStream = plugin.getResource(configFilePath);
            if (defIMessagesStream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defIMessagesStream, Charsets.UTF_8)));
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Could not reload config at " + this.getConfigFilePath());
            ex.printStackTrace();
        }
    }

    // Getters

    public YamlConfiguration getConfig() {
        return config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    // Setters

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
