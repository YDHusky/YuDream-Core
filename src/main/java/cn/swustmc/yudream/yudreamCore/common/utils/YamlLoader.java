package cn.swustmc.yudream.yudreamCore.common.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class YamlLoader {

    private static YamlLoader instance;

    public static YamlLoader getInstance() {
        if (instance == null) {
            instance = new YamlLoader();
        }
        return instance;
    }

    public FileConfiguration load(JavaPlugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);
        plugin.saveResource(file.getName(), false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void save(JavaPlugin plugin, String path, FileConfiguration config) throws IOException {
        File file = new File(plugin.getDataFolder(), path);
        config.save(file);
    }

    public FileConfiguration reload(JavaPlugin plugin, String path) {
        plugin.getLogger().info("Reloading " + path);
        return load(plugin, path);
    }
}
