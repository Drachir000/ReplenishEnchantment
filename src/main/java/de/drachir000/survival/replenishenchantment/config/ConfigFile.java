package de.drachir000.survival.replenishenchantment.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class ConfigFile {

    private final Plugin plugin;
    private final String resourcePath;
    private FileConfiguration config;

    public ConfigFile(Plugin plugin, String resourcePath) {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        reload();
    }

    public void reload() {

        File configFile = new File(this.plugin.getDataFolder(), resourcePath);

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.plugin.saveResource(resourcePath, false);
        }

        this.config = new YamlConfiguration();

        try {
            this.config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            this.plugin.getLogger().log(Level.SEVERE, "Exception while loading configuration file \"" + resourcePath + "\"");
        }

    }

    public FileConfiguration getConfig() {
        if (this.config == null) reload();
        return this.config;
    }

}
