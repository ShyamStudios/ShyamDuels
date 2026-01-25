package dev.piyush.shyamduels.config;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private final ShyamDuels plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public ConfigManager(ShyamDuels plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessages() {
        if (messagesConfig == null) {
            loadMessages();
        }
        return messagesConfig;
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        loadMessages();
    }

    public boolean isDebug() {
        return plugin.getConfig().getBoolean("debug", false);
    }
}
