package cn.swustmc.yudream.yudreamCore.entity;

import cn.swustmc.yudream.yudreamCore.common.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * cn.swustmc.yudream.yudreamCore.entity
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class Message {
    private final TextComponent.@NotNull Builder cb;

    public Message() {
        this.cb = Component.text();
    }

    public Message text(String text) {
        try {
            cb.append(Component.text(StringUtils.replaceColor(text)));
        } catch (Exception ignored) {

        }
        return this;
    }

    public Message command(String text, String command) {
        try {
            cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
        } catch (Exception ignored) {

        }
        return this;

    }

    public Message appendMessage(Message message) {
        cb.append(message.build());
        return this;
    }

    public Message hover(String text) {
        try {
            cb.hoverEvent(Component.text(StringUtils.replaceColor(text)));
        } catch (Exception ignored) {

        }
        return this;
    }

    public Message color(TextColor color) {
        cb.color(color);
        return this;
    }

    public Message hoverText(String text, String hoverText) {
        try {
            cb.append(Component.text(StringUtils.replaceColor(text)).hoverEvent(Component.text(StringUtils.replaceColor(hoverText))));
        } catch (Exception ignored) {

        }
        return this;
    }

    public Message runCommand(String text, String command) {
        try {
            cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        } catch (Exception ignored) {

        }
        return this;

    }

    public Message url(String text, String url) {
        try {
            cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url)));
        } catch (Exception ignored) {
        }
        return this;

    }

    public Message newLine() {
        cb.append(Component.newline());
        return this;

    }

    public Message appendComponent(Component component) {
        cb.append(component);
        return this;
    }

    public Component build() {
        return cb.build();
    }


    public static Component mm(String miniMessageString) {
        return MiniMessage.miniMessage().deserialize(miniMessageString);
    }

}
