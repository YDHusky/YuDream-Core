package cn.swustmc.husky.huskyCore.api.command;

import cn.swustmc.husky.huskyCore.enums.CommandSenderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * cn.swustmc.husky.huskyCore.api.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HuskyCommand {
    String descLangKey() default "";

    String desc() default ""; // 不存在lang时使用

    CommandSenderType senderType() default CommandSenderType.ALL;

    String baseCommand();

    String[] args() default {};

    String permission() default "";
}
