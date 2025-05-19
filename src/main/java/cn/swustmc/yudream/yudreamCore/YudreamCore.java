package cn.swustmc.yudream.yudreamCore;

import cn.swustmc.yudream.yudreamCore.api.plugin.CorePlugin;
import cn.swustmc.yudream.yudreamCore.common.YuDreamLoader;
import cn.swustmc.yudream.yudreamCore.module.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@CorePlugin
public final class YudreamCore extends JavaPlugin {
    public static YudreamCore instance;

    @Override
    public void onEnable() {
        instance = this;
        YuDreamLoader.loader();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
