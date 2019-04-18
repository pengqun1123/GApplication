package com.TG.library.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串判空操作
 *
 * @author kitty
 */
public class StringEmpty {

    public static boolean checkEmpty(String str) {
        if (str == null || str.equals("") || str.length() <= 0) {
            return true;
        }

        return false;
    }

    public static boolean isNotEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str)) {
            return false;
        }
        return true;
    }

    public static String getStr(String str) {
        if (str == null || str.equals("") || str.length() <= 0) {
            return "";
        }
        return str;
    }


    /*
     * 验证号码 手机号 固话均可
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号  
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    //使用String的split 方法  ,将字符串转为数组
    public static String[] convertStrToArray(String str, String fh) {
        String[] strArray = null;
        strArray = str.split(fh); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    /**
     * 校验是否是json格式
     *
     * @param json
     * @return
     */
//    public static boolean isGoodJson(String json) {
//
//        if (json == null || "".equals(json) || json.equals("-1")) {
//            return false;
//        }
//        try {
//            new JsonParser().parse(json);
//            new JSONObject(json);
//            return true;
//        } catch (JsonParseException e) {
//            System.out.println("bad json: " + json);
//            return false;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

}
