package cn.swustmc.yudream.yudreamCore.command;

import cn.swustmc.yudream.yudreamCore.api.command.BaseCommand;
import cn.swustmc.yudream.yudreamCore.api.command.YuDreamCommand;
import org.bukkit.command.CommandSender;

/**
 * cn.swustmc.husky.huskyCore.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */

@YuDreamCommand(baseCommand = "huskycore", desc = "测试命令")
public class TestCommand implements BaseCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {

        return false;
    }
}
