/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils.repository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰法-下划线互转
 *
 * @author liyong
 * @since 2015.07.04
 * @version 1.0.0
 */
public class UnderlineCamelUtil {

    private static final String UNDERLINE = "([A-Za-z\\d]+)(_)?";
    private static final Pattern PATTERN_UNDERLINE = Pattern.compile(UNDERLINE);

    private static final String CAMEL = "[A-Z]([a-z\\d]+)?";
    private static final Pattern PATTERN_CAMEL = Pattern.compile(CAMEL);

    /**
     * 下划线转驼峰法
     *
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN_UNDERLINE.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN_CAMEL.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        String line = "I_HAVE_AN_IPANG3_PIG";
//        String camel = underline2Camel(line, false);
//        System.out.println(camel);
//        System.out.println(camel2Underline(camel));
//        
        System.out.println(camel2Underline("HelloWorld"));
        System.out.println(underline2Camel("hello_world", false));

    }
}
