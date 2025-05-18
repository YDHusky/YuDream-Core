package cn.swustmc.husky.huskyCore.api.command;

import org.bukkit.command.CommandSender;

/**
 * cn.swustmc.husky.huskyCore.api.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public interface BaseCommand {
    boolean execute(CommandSender sender, String[] args);
}
