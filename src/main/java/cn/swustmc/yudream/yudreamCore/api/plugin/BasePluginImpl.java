package cn.swustmc.yudream.yudreamCore.api.plugin;

import cn.swustmc.yudream.yudreamCore.common.YuDreamLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * cn.swustmc.yudream.yudreamCore.api.plugin
 *
 * @author SiberianHusky
 * * @date 2025/5/20
 */
public class BasePluginImpl extends JavaPlugin implements BasePlugin {
    public static BasePluginImpl instance;

    @Override
    public void registerAll() {
        YuDreamLoader.loader(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        registerAll();
    }
}
