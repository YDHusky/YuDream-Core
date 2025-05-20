package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.api.event.CoreEvent;
import cn.swustmc.yudream.yudreamCore.common.utils.ScanUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cn.swustmc.yudream.yudreamCore.module
 *
 * @author SiberianHusky
 * * @date 2025/5/21
 */
public class EventManager {
    private static EventManager instance;

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }


    public void registerPluginEvent(Plugin plugin) {
        List<Class<?>> classes = ScanUtils.scanClassByAnnotation(plugin, CoreEvent.class, plugin.getName());
        for (Class<?> clazz : classes) {
            try {
                if (Listener.class.isAssignableFrom(clazz)) {
                    plugin.getServer().getPluginManager().registerEvents((Listener) clazz.newInstance(), plugin);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("无法加载事件类: " + clazz.getName() + Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
