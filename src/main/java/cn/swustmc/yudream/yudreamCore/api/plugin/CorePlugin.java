package cn.swustmc.yudream.yudreamCore.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cn.swustmc.yudream.yudreamCore.api.plugin
 *
 * @author SiberianHusky
 * * @date 2025/5/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CorePlugin {
    String scanPackage() default "";

    boolean isRegisterCommand() default true;

    boolean isRegisterGui() default false;

    boolean isRegisterDefaultConfig() default false;

    String[] registerConfigPath() default {};

    String baseLangFolder() default "/lang";

    String[] registerLang() default {}; // 文件名去后缀(.yml)
}
