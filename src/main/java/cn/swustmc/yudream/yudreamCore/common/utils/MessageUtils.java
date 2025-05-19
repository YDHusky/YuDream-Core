package cn.swustmc.yudream.yudreamCore.common.utils;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

/**
 * cn.swustmc.yudream.yudreamCore.common.utils
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class MessageUtils {
    private static MessageUtils instance;

    public static MessageUtils getInstance() {
        if (instance == null) {
            instance = new MessageUtils();
        }
        return instance;
    }

    public void sendAllPlayer(Component message) {
        ForwardingAudience audience = Bukkit.getServer();
        audience.sendMessage(message);
    }

}
