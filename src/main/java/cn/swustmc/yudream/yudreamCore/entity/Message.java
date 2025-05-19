package cn.swustmc.yudream.yudreamCore.entity;

import cn.swustmc.yudream.yudreamCore.common.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.units.qual.N;
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
        cb.append(Component.text(StringUtils.replaceColor(text)));
        return this;
    }

    public Message command(String text, String command) {
        cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
        return this;

    }

    public Message appendMessage(Message message) {
        cb.append(message.build());
        return this;
    }

    public Message hover(String text) {
        cb.hoverEvent(Component.text(StringUtils.replaceColor(text)));
        return this;
    }

    public String color(String color) {
        cb.color(net.kyori.adventure.text.format.TextColor.fromHexString(color));
        return color;
    }

    public Message hoverText(String text, String hoverText) {
        cb.append(Component.text(StringUtils.replaceColor(text)).hoverEvent(Component.text(StringUtils.replaceColor(hoverText))));
        return this;
    }

    public Message runCommand(String text, String command) {
        cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        return this;

    }

    public Message url(String text, String url) {
        cb.append(Component.text(StringUtils.replaceColor(text)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url)));
        return this;

    }

    public Message newLine() {
        cb.append(Component.newline());
        return this;

    }

    public Component build() {
        return cb.build();
    }

    public static Component mm(String miniMessageString) {
        return MiniMessage.miniMessage().deserialize(miniMessageString);
    }

}
