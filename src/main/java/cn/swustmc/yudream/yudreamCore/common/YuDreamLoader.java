package cn.swustmc.yudream.yudreamCore.common;

import cn.swustmc.yudream.yudreamCore.YudreamCore;
import cn.swustmc.yudream.yudreamCore.api.plugin.CorePlugin;
import cn.swustmc.yudream.yudreamCore.module.CommandManager;
import cn.swustmc.yudream.yudreamCore.module.ConfigManager;
import cn.swustmc.yudream.yudreamCore.module.LangManager;
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
            YudreamCore.instance.getLogger().info("检测到基于YuDreamCore的插件[" + javaPlugin.getName() + "]即将开始加载!");
            YudreamCore.instance.getLogger().info("""
                    \n[%s]插件配置信息:
                    ==================================
                    版本: %s
                    作者: %s
                    扫描包名: %s
                    是否注册命令: %s
                    是否注册默认配置: %s
                    其他配置注册: %s
                    语言文件路径: %s
                    语言注册: %s
                    ==================================
                    """.formatted(javaPlugin.getName(),
                    javaPlugin.getPluginMeta().getVersion(),
                    javaPlugin.getPluginMeta().getAuthors(),
                    scanPackage,
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
                    e.printStackTrace();
                }
            }
            YudreamCore.instance.getLogger().info("已加载 " + javaPlugin.getName() + " 插件!");
        }

    }

    public static void loader(){
        for (Plugin plugin : YudreamCore.instance.getServer().getPluginManager().getPlugins()) {
            JavaPlugin javaPlugin = (JavaPlugin) plugin;
            loadPlugin(javaPlugin);
        }
    }
}
