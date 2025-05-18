package cn.swustmc.husky.huskyCore.module;

import cn.swustmc.husky.huskyCore.common.utils.YamlLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * ConfigManager 类用于管理插件的配置文件
 * 它提供了注册默认配置和自定义配置的方法，以及重新加载配置的方法
 */
public class ConfigManager {
    // 存储默认配置的映射表
    public Map<String, FileConfiguration> defaultConfigMap;
    // 存储所有配置的映射表，键是插件名，值是另一个映射表，其中键是配置路径，值是配置文件
    public Map<String, Map<String, FileConfiguration>> configMap;

    /**
     * 注册插件的默认配置
     * 这个方法会保存插件的默认配置，并将其添加到 defaultConfigMap 中
     *
     * @param plugin 插件实例，用于获取插件的名称和配置
     */
    public void registerDefaultConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        defaultConfigMap.put(plugin.getName(), plugin.getConfig());
    }

    /**
     * 注册插件的自定义配置
     * 如果该插件还没有任何配置被注册，将会为它创建一个新的配置映射表
     * 然后将指定路径的配置文件加载并添加到配置映射表中
     *
     * @param plugin 插件实例，用于获取插件的名称
     * @param path   配置文件的路径
     */
    public void registerConfig(JavaPlugin plugin, String path) {
        if (!configMap.containsKey(plugin.getName())) {
            configMap.put(plugin.getName(), new HashMap<>());
        }
        configMap.get(plugin.getName()).put(path, YamlLoader.getInstance().load(plugin, path));
    }

    /**
     * 重新加载插件的指定配置
     * 这个方法会从指定路径重新加载配置文件，并更新到配置映射表中
     *
     * @param plugin 插件实例，用于获取插件的名称
     * @param path   配置文件的路径
     */
    public void reloadConfig(JavaPlugin plugin, String path) {
        configMap.get(plugin.getName()).put(path, YamlLoader.getInstance().reload(plugin, path));
    }

    /**
     * 重新加载插件的默认配置
     * 这个方法会重新加载插件的默认配置，并将其更新到 defaultConfigMap 中
     *
     * @param plugin 插件实例，用于重新加载默认配置
     */
    public void reloadDefaultConfig(JavaPlugin plugin) {
        plugin.reloadConfig();
        defaultConfigMap.put(plugin.getName(), plugin.getConfig());
    }

    /**
     * 重新加载插件的所有配置，包括默认配置和自定义配置
     * 如果插件没有注册任何配置，将会抛出运行时异常
     * 这个方法会遍历插件的所有配置，并调用 registerConfig 方法重新加载它们
     * 最后重新加载默认配置，并记录日志信息
     *
     * @param plugin 插件实例，用于获取插件的名称和重新加载所有配置
     */
    public void reloadAllConfig(JavaPlugin plugin) {
        if (!configMap.containsKey(plugin.getName())) {
            throw new RuntimeException(plugin.getName() + "插件没有注册任何配置文件!");
        }
        Map<String, FileConfiguration> configs = configMap.get(plugin.getName());
        for (String path : configs.keySet()) {
            registerConfig(plugin, path);
        }
        reloadDefaultConfig(plugin);
        plugin.getLogger().info("所有配置被重载!" + plugin.getName());
    }
}
