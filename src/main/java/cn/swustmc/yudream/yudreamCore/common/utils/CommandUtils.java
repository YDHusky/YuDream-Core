package cn.swustmc.yudream.yudreamCore.common.utils;

import cn.swustmc.yudream.yudreamCore.api.command.BaseCommand;
import cn.swustmc.yudream.yudreamCore.api.command.YuDreamCommand;
import cn.swustmc.yudream.yudreamCore.entity.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * cn.swustmc.yudream.yudreamCore.common.utils
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class CommandUtils {
    public static Component commandHelpMenu(JavaPlugin plugin, List<BaseCommand> commands) {
        Message msg = new Message().text("&1======================").text("&b命令帮助菜单").text("&1======================").newLine();
        for (BaseCommand command : commands) {
            msg.appendMessage(command.helpLineInfo(plugin, command.getClass().getAnnotation(YuDreamCommand.class))).newLine();
        }
        return msg.build();
    }
}
