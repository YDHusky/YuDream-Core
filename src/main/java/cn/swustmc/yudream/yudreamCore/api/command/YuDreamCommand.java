package cn.swustmc.yudream.yudreamCore.api.command;

import cn.swustmc.yudream.yudreamCore.enums.CommandSenderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cn.swustmc.husky.huskyCore.api.command
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YuDreamCommand {
    String descLangKey() default "";

    String desc() default ""; // 不存在lang时使用

    CommandSenderType senderType() default CommandSenderType.ALL;

    String baseCommand();

    String[] args() default {};

    String usages() default  ""; // 命令用法

    String permission() default "";
}
