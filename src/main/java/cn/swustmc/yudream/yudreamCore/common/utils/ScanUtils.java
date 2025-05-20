package cn.swustmc.yudream.yudreamCore.common.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * cn.swustmc.yudream.yudreamCore.common.utils
 * 扫描工具包
 *
 * @author SiberianHusky
 * * @date 2025/5/21
 */
public class ScanUtils {
    public static List<Class<?>> scanClassByAnnotation(Plugin plugin, Class<?> annotationClass, String packageName) {
        try (ScanResult scanResult = new ClassGraph().addClassLoader(plugin.getClass().getClassLoader()).enableAnnotationInfo().acceptPackages(packageName).scan()) {
            return scanResult.getClassesWithAnnotation(annotationClass.getName()).loadClasses();
        }
    }

    public static List<Class<?>> scanAllClass(Plugin plugin, String packageName) {
        try (ScanResult scanResult = new ClassGraph().addClassLoader(plugin.getClass().getClassLoader()).acceptPackages(packageName).scan()) {
            return scanResult.getAllClasses().loadClasses();
        }
    }
}
