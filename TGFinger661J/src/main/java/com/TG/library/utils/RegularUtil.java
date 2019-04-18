package com.TG.library.utils;

/**
 * Created By pq
 * on 2019/4/12
 * 正则表达式工具类
 */
public class RegularUtil {

    /**
     * 判断字符串中包含数字
     */
    public static boolean strContainsNum(String str) {
        return str.matches("^[0-9]+$");
    }

    /**
     * 判断字符串中包含字母
     */
    public static boolean strContainsAlphabet(String str) {
        return str.matches("^[A-Za-z]+$");
    }

    /**
     * 判断字符串中包含中文
     */
    public static boolean strContainsChinese(String str) {
        return str.matches("^[u4e00-\u9fa5]+$");
    }

    /**
     * 判断字符串中仅包含数字，字母，中文
     */
    public static boolean strContainsNumAlpChin(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    /**
     * 判断字符串仅包含字母或数字或中文
     */
    public static boolean strContainsNumOrAlpOrChin(String str) {
        boolean b = strContainsNum(str) || strContainsAlphabet(str)
                || strContainsChinese(str) || strContainsNumAlpChin(str);
        return b;
    }

}
