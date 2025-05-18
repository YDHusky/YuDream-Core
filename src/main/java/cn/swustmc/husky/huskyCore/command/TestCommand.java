package cn.swustmc.husky.huskyCore.command;

import cn.swustmc.husky.huskyCore.api.command.BaseCommand;
import cn.swustmc.husky.huskyCore.api.command.HuskyCommand;
import org.bukkit.command.CommandSender;

/**
 * cn.swustmc.husky.huskyCore.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */

@HuskyCommand(baseCommand = "huskycore", desc = "测试命令")
public class TestCommand implements BaseCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {

        return false;
    }
}
