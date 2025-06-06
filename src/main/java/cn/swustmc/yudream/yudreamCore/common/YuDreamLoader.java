package cn.swustmc.yudream.yudreamCore.common;

import cn.swustmc.yudream.yudreamCore.YudreamCore;
import cn.swustmc.yudream.yudreamCore.api.plugin.CorePlugin;
import cn.swustmc.yudream.yudreamCore.module.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * cn.swustmc.yudream.yudreamCore.common
 *
 * @author SiberianHusky
 * * @date 2025/5/20
 */
public class YuDreamLoader {
    private static void loadPlugin(JavaPlugin javaPlugin) {
        CorePlugin corePlugin = javaPlugin.getClass().getAnnotation(CorePlugin.class);
        if (corePlugin != null) {
            String scanPackage = corePlugin.scanPackage().isEmpty() ? javaPlugin.getClass().getPackageName() : corePlugin.scanPackage();
            YudreamCore.instance.getLogger().info("检测到基于YuDreamCore的插件[" + javaPlugin.getName() + "], 即将开始加载!");
            YudreamCore.instance.getLogger().info("""
                    ===========================================
                        版本: %s
                        作者: %s
                        扫描包名: %s
                        是否注册GUI: %s,
                        是否注册事件: %s,
                        是否注册命令: %s
                        是否注册默认配置: %s
                        其他配置注册: %s
                        语言文件路径: %s
                        语言注册: %s
                    ===========================================
                    """.formatted(
                    javaPlugin.getPluginMeta().getVersion(),
                    javaPlugin.getPluginMeta().getAuthors(),
                    scanPackage,
                    corePlugin.isRegisterGui(),
                    corePlugin.isRegisterEvent(),
                    corePlugin.isRegisterCommand(),
                    corePlugin.isRegisterDefaultConfig(),
                    String.join(",", corePlugin.registerConfigPath()),
                    corePlugin.baseLangFolder(),
                    String.join(",", corePlugin.registerLang())
            ));

            if (corePlugin.isRegisterDefaultConfig()) {
                ConfigManager.getInstance().registerDefaultConfig(javaPlugin);
            }

            for (String path : corePlugin.registerConfigPath()) {
                ConfigManager.getInstance().registerConfig(javaPlugin, path);
            }

            for (String lang : corePlugin.registerLang()) {
                LangManager.getInstance().registerLang(javaPlugin, corePlugin.baseLangFolder(), lang);
            }

            if (corePlugin.isRegisterCommand()) {
                try {
                    CommandManager.getInstance().loadCommand(javaPlugin, scanPackage);
                } catch (Exception e) {
                    YudreamCore.instance.getLogger().warning("注册指令出现问题! " + e.getMessage());
                }
            }
            if (corePlugin.isRegisterGui()) {
                try {
                    GuiManager.getInstance().loadGuis(javaPlugin, scanPackage);
                } catch (Exception e) {
                    YudreamCore.instance.getLogger().warning("注册GUI出现问题! " + e.getMessage());
                }
            }
            if (corePlugin.isRegisterEvent()) {
                YudreamCore.instance.getLogger().info("正在注册事件...");
                EventManager.getInstance().registerPluginEvent(javaPlugin);
                YudreamCore.instance.getLogger().info("已注册 " + javaPlugin.getName() + " 插件!");
            }
            YudreamCore.instance.getLogger().info("已加载 " + javaPlugin.getName() + " 插件!");
        }

    }

    public static void loader() {
        for (Plugin plugin : YudreamCore.instance.getServer().getPluginManager().getPlugins()) {
            JavaPlugin javaPlugin = (JavaPlugin) plugin;
            loadPlugin(javaPlugin);
        }
    }
}
