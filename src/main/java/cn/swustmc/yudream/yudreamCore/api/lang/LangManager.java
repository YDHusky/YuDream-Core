package cn.swustmc.yudream.yudreamCore.api.lang;

import cn.swustmc.yudream.yudreamCore.common.utils.StringUtils;
import cn.swustmc.yudream.yudreamCore.common.utils.YamlLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个用于管理语言配置的类，允许动态加载、切换和使用不同的语言文件
 */
public class LangManager {
    private final JavaPlugin plugin;
    private final String baseLangFolder;
    private final Map<String, FileConfiguration> langMap;
    private String currentLang;

    /**
     * 构造一个LangManager实例，使用默认的语言文件夹路径
     *
     * @param plugin      Bukkit插件实例，用于日志输出和资源加载
     * @param defaultLang 默认使用的语言标识符
     */
    public LangManager(JavaPlugin plugin, String defaultLang) {
        this.plugin = plugin;
        this.baseLangFolder = "lang/";
        this.currentLang = defaultLang;
        langMap = new HashMap<>();
    }

    /**
     * 构造一个LangManager实例，允许自定义语言文件夹路径
     *
     * @param plugin         Bukkit插件实例，用于日志输出和资源加载
     * @param baseLangFolder 语言文件的基础文件夹路径
     * @param defaultLang    默认使用的语言标识符
     */
    public LangManager(JavaPlugin plugin, String baseLangFolder, String defaultLang) {
        this.plugin = plugin;
        this.baseLangFolder = baseLangFolder;
        this.currentLang = defaultLang;
        langMap = new HashMap<>();
    }

    /**
     * 注册一个新的语言到langMap中
     *
     * @param lang 语言标识符，用于标识特定的语言文件
     */
    public void registerLang(String lang) {
        this.plugin.getLogger().info("注册语言: " + lang);
        String langFile = baseLangFolder + lang + ".yml";
        langMap.put(lang, YamlLoader.getInstance().load(plugin, langFile));
    }

    /**
     * 重新加载所有已注册的语言文件，用于在运行时更新语言设置
     */
    public void reloadLang() {
        this.plugin.getLogger().info("重载语言中!");
        langMap.replaceAll((k, v) -> YamlLoader.getInstance().reload(plugin, k));
        plugin.getLogger().info("语言重载完成!");
    }

    /**
     * 切换当前使用的语言
     *
     * @param lang 要切换到的语言标识符
     * @throws RuntimeException 如果尝试切换到一个未注册的语言，则抛出运行时异常
     */
    public void switchLang(String lang) {
        if (!langMap.containsKey(lang)) {
            currentLang = lang;
        } else {
            throw new RuntimeException("不存在的语言" + lang);
        }
    }

    /**
     * 获取当前语言中与给定键关联的字符串值
     *
     * @param langKey 语言文件中的键，用于查找对应的字符串值
     * @return 与给定键关联的字符串值
     * @throws RuntimeException 如果当前语言未注册，则抛出运行时异常
     */
    public String getLang(String langKey, Object... args) {
        if (langMap.containsKey(currentLang)) {
            return StringUtils.replacePlaceholder(langMap.get(currentLang).getString(langKey), args);
        }
        throw new RuntimeException("不存在的语言" + langKey);
    }
}
