package cn.swustmc.yudream.yudreamCore.common.utils;

import cn.swustmc.yudream.yudreamCore.YudreamCore;
import cn.swustmc.yudream.yudreamCore.api.command.BaseCommand;
import cn.swustmc.yudream.yudreamCore.api.command.YuDreamCommand;
import cn.swustmc.yudream.yudreamCore.entity.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * cn.swustmc.yudream.yudreamCore.common.utils
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class CommandUtils {
    public static Component commandHelpMenu(JavaPlugin plugin, List<BaseCommand> commands) {
        Message msg = new Message().appendComponent(Component.text("======================").color(TextColor.fromCSSHexString("#8bc34b"))).appendComponent(
                Component.text("命令帮助菜单").color(TextColor.fromCSSHexString("#4cae4f"))
        ).appendComponent(
                Component.text("======================").color(TextColor.fromCSSHexString("#8bc34b"))
        ).newLine();
        for (BaseCommand command : commands) {
            msg.appendMessage(command.helpLineInfo(plugin, command.getClass().getAnnotation(YuDreamCommand.class))).newLine();
        }
        return msg.build();
    }

    public static PluginCommand getCommand(JavaPlugin plugin, String baseCommand) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<PluginCommand> pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        pluginCommandConstructor.setAccessible(true);
        PluginCommand command = pluginCommandConstructor.newInstance(baseCommand, plugin);
        command.setName(baseCommand);
        command.setUsage("/" + baseCommand + " help");
        command.setDescription("YuDreamCore 命令");
        return command;
    }
}
