package cn.swustmc.husky.huskyCore.common.utils;

/**
 * cn.swustmc.husky.huskyCore.common.utils
 *
 * @author SiberianHusky
 * * @date 2025/5/19
 */
public class StringUtils {
    public static String replaceColor(String str) {
        return str.replace("&", "§");
    }

    /**
     * 替换字符串中的占位符
     *
     * @param str  原始字符串，可能包含占位符
     * @param args 一对对的替换项，首先是键（占位符），然后是对应的值
     * @return 替换后的字符串
     * @throws IllegalArgumentException 如果参数为空或参数长度为奇数，抛出此异常
     */
    public static String replacePlaceholder(String str, Object... args) {
        if (str == null || args == null || args.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须成对出现: key1, value1, key2, value2...");
        }
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i].toString();
            String value = args[i + 1].toString();
            str = str.replace("{" + key + "}", value);
        }
        return replaceColor(str);
    }


}
