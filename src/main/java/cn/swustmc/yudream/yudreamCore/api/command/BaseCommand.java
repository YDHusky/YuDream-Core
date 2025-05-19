package cn.swustmc.yudream.yudreamCore.api.command;

import cn.swustmc.yudream.yudreamCore.entity.Message;
import cn.swustmc.yudream.yudreamCore.module.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * cn.swustmc.husky.huskyCore.api.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public interface BaseCommand {
    boolean execute(CommandSender sender, String[] args);

    default Message helpLineInfo(JavaPlugin plugin, YuDreamCommand yuDreamCommand) {
        String desc = "";
        try {
            desc = LangManager.getInstance().getLang(plugin, yuDreamCommand.descLangKey());
        } catch (Exception e) {
            desc = yuDreamCommand.desc();
        }
        return new Message().color(TextColor.fromCSSHexString("#cbdc38")).command(yuDreamCommand.usages(), "/" + yuDreamCommand.baseCommand() + " " + String.join(" ", yuDreamCommand.args())).text(" ").appendComponent(Component.text(desc).color(TextColor.fromCSSHexString("#AAAAAA")));
    }
}
