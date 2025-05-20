package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.api.event.CoreEvent;
import cn.swustmc.yudream.yudreamCore.api.gui.InventoryGui;
import cn.swustmc.yudream.yudreamCore.api.gui.YuDreamGui;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@CoreEvent
public class GuiManager implements Listener {
    private static GuiManager instance;

    public static GuiManager getInstance() {
        if (instance == null) {
            instance = new GuiManager();
        }
        return instance;
    }

    private final Map<String ,InventoryGui> guiList = new HashMap<>();

    public void openGUI(Player player, String name, Object... args) {
        player.openInventory(guiList.get(name).setData(args).getInventory());
    }

    public void loadGuis(JavaPlugin plugin, String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .addClassLoader(plugin.getClass().getClassLoader())
                .acceptPackages(packageName)
                .scan()) {
            List<ClassInfo> annotatedClasses = scanResult.getAllClasses();
            for (ClassInfo classInfo : annotatedClasses) {
                Class<?> clazz = classInfo.loadClass();
                if (InventoryGui.class.isAssignableFrom(clazz)) {
                    try {
                        InventoryGui commandInstance = (InventoryGui) clazz.getDeclaredConstructor().newInstance();
                        YuDreamGui annotation = clazz.getAnnotation(YuDreamGui.class);
                        String name = annotation.name();
                        this.guiList.put(name, commandInstance);
                        plugin.getLogger().info("已加载GUI: " + commandInstance.getClass().getName());
                    } catch (Exception e) {
                        plugin.getLogger().severe("无法加载GUI类: " + clazz.getName());
                        plugin.getLogger().warning(Arrays.toString(e.getStackTrace()));
                    }
                }
            }
        }
        plugin.getLogger().info("所有GUI被注册! 已注册" + guiList.size() + "个GUI!");
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        for (InventoryGui gui : guiList.values()) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder == null || holder.getClass() != gui.getClass()) return;
            gui.onOpen(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        for (InventoryGui gui : guiList.values()) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder == null || holder.getClass() != gui.getClass()) return;
            gui.onClose(event);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        for (InventoryGui gui : guiList.values()) {
            InventoryHolder holder = event.getSource().getHolder();
            if (holder == null || holder.getClass() != gui.getClass()) return;
            gui.onMoveItem(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (InventoryGui gui : guiList.values()) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder == null || holder.getClass() != gui.getClass()) return;
            gui.onDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (InventoryGui gui : guiList.values()) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder == null || holder.getClass() != gui.getClass()) return;
            gui.onClick(event);
        }
    }
}
