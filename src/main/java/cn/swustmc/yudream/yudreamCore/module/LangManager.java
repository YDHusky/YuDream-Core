package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.entity.lang.PluginLang;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * cn.swustmc.yudream.yudreamCore.module
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class LangManager {
    private static LangManager instance;

    public static LangManager getInstance() {
        if (instance == null) {
            instance = new LangManager();
        }
        return instance;
    }

    private final Map<String, PluginLang> pluginLangMap;

    private LangManager() {
        pluginLangMap = new HashMap<>();
    }

    public void registerLang(JavaPlugin plugin, String defaultLang) {
        PluginLang pluginLang = new PluginLang(plugin, defaultLang);
        pluginLangMap.put(plugin.getName(), pluginLang);
    }

    public void registerLang(JavaPlugin plugin, String baseLangFolder, String defaultLang) {
        PluginLang pluginLang = new PluginLang(plugin, baseLangFolder, defaultLang);
        pluginLangMap.put(plugin.getName(), pluginLang);
    }

    public void reloadLang(JavaPlugin plugin) {
        PluginLang pluginLang = pluginLangMap.get(plugin.getName());
        if (pluginLang != null) {
            pluginLang.reloadLang();
        }
    }

    public void switchLang(JavaPlugin plugin, String lang) {
        PluginLang pluginLang = pluginLangMap.get(plugin.getName());
        if (pluginLang != null) {
            pluginLang.switchLang(lang);
        }
    }

    public String getLang(JavaPlugin plugin, String langKey, Object... args) {
        PluginLang pluginLang = pluginLangMap.get(plugin.getName());
        if (pluginLang != null) {
            return pluginLang.getLang(langKey, args);
        }
        throw new RuntimeException("未注册的插件!");
    }


}
