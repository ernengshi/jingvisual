/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * http://blog.csdn.net/xiangyihu/article/details/77712156
 *
 * @author liyong
 */
public class NumberUtil {

    /**
     * 判断当前值是否为整数
     *
     * @param value
     * @return
     */
    public static boolean isInteger(Object value) {
        if (StringUtils.isEmpty(String.valueOf(value))) {
            return false;
        }
        String mstr = String.valueOf(value);
        Pattern pattern = Pattern.compile("^-?\\d+{1}");
        return pattern.matcher(mstr).matches();
    }

    /**
     * 判断当前值是否为数字(包括小数)
     *
     * @param value
     * @return
     */
    public static boolean isDigit(Object value) {
        if (StringUtils.isEmpty(String.valueOf(value))) {
            return false;
        }
        String mstr = String.valueOf(value);
        Pattern pattern = Pattern.compile("^-?[0-9]*.?[0-9]*{1}");
        return pattern.matcher(mstr).matches();
    }

    /**
     * 将数字格式化输出
     *
     * @param value 需要格式化的值
     * @param precision 精度(小数点后的位数)
     * @return
     */
    public static String format(Object value, Integer precision) {
        Double number = 0.0;
        if (NumberUtil.isDigit(value)) {
            number = new Double(value.toString());
        }
        precision = (precision == null || precision < 0) ? 2 : precision;
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.setScale(precision, BigDecimal.ROUND_HALF_UP)//四舍五入，2.35变成2.4
                .toString();
    }

    /**
     * 将数字格式化输出
     *
     * @param value 需要格式化的值
     * @param precision 精度(小数点后的位数)
     * @return
     */
    public static Double formatDouble(Object value, Integer precision) {
        Double number = 0.0;
        if (NumberUtil.isDigit(value)) {
            number = new Double(value.toString());
        }
        precision = (precision == null || precision < 0) ? 2 : precision;
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.setScale(precision, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     *
     * 去除多余的小数点，直接舍弃
     *
     * @param value
     * @param precision
     * @return
     * @version v1.0
     * @author 徐高波
     * @date 2016年9月19日 下午1:07:37
     */
    public static Double formatDoubleDown(Object value, Integer precision) {
        Double number = 0.0;
        if (NumberUtil.isDigit(value)) {
            number = new Double(value.toString());
        }
        precision = (precision == null || precision < 0) ? 2 : precision;
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.setScale(precision, BigDecimal.ROUND_DOWN)
                .doubleValue();
    }

    /**
     * 将数字格式化输出
     *
     * @param value
     * @return
     */
    public static String format(Object value) {
        return NumberUtil.format(value, 2);
    }

    /**
     * 将值转成Integer型，如果不是整数，则返回0
     *
     * @param value
     * @param replace 如果为0或者null，替换值
     * @return
     */
    public static Integer parseInteger(Object value, Integer replace) {
        if (value == null) {
            return replace;
        }
        if (!NumberUtil.isInteger(value)) {
            return replace;
        }
        return new Integer(value.toString());
    }

    /**
     * 将值转成Integer型，如果不是整数，则返回0
     *
     * @param value
     * @return
     */
    public static Integer parseInteger(Object value) {
        return NumberUtil.parseInteger(value, 0);
    }

    /**
     * 将值转成Long型
     *
     * @param value
     * @param replace 如果为0或者null，替换值
     * @return
     */
    public static Long parseLong(Object value, Long replace) {
        if (!NumberUtil.isInteger(value)) {
            return replace;
        }
        return new Long(value.toString());
    }

    /**
     * 将值转成Long型，如果不是整数，则返回0
     *
     * @param value
     * @return
     */
    public static Long parseLong(Object value) {
        return NumberUtil.parseLong(value, 0L);
    }

    /**
     * 将值转成Double型
     *
     * @param value
     * @param replace replace 如果为0或者null，替换值
     * @return
     */
    public static Double parseDouble(Object value, Double replace) {
        if (!NumberUtil.isDigit(value)) {
            return replace;
        }
        return new Double(value.toString());
    }

    /**
     * 将值转成Double型，如果不是整数，则返回0
     *
     * @param value
     * @return
     */
    public static Double parseDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        return NumberUtil.parseDouble(value, 0.0);
    }

    /**
     * 将char型数据转成字节数组
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(char value) {
        byte[] bt = new byte[2];
        for (int i = 0; i < bt.length; i++) {
            bt[i] = (byte) (value >>> (i * 8));
        }
        return bt;
    }

    /**
     * 将short型数据转成字节数组
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(short value) {
        byte[] bt = new byte[2];
        for (int i = 0; i < bt.length; i++) {
            bt[i] = (byte) (value >>> (i * 8));
        }
        return bt;
    }

    /**
     * 将int型数据转成字节数组
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(int value) {
        byte[] bt = new byte[4];
        for (int i = 0; i < bt.length; i++) {
            bt[i] = (byte) (value >>> (i * 8));
        }
        return bt;
    }

    /**
     * 将long型数据转成字节数组
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(long value) {
        byte[] bt = new byte[8];
        for (int i = 0; i < bt.length; i++) {
            bt[i] = (byte) (value >>> (i * 8));
        }
        return bt;
    }

    /**
     * 将short型数据插入到指定索引的字节数组中
     *
     * @param index 索引
     * @param values 字节数组
     * @param value 需要插入的值
     */
    public static void insert(int index, byte[] values, short value) {
        byte[] bt = NumberUtil.toBytes(value);
        System.arraycopy(bt, 0, values, index, 2);
    }

    /**
     * 将int型数据插入到指定索引的字节数组中
     *
     * @param index 索引
     * @param values 字节数组
     * @param value 需要插入的值
     */
    public static void insert(int index, byte[] values, int value) {
        byte[] bt = NumberUtil.toBytes(value);
        System.arraycopy(bt, 0, values, index, 4);
    }

    /**
     * 将long型数据插入到指定索引的字节数组中
     *
     * @param index 索引
     * @param values 字节数组
     * @param value 需要插入的值
     */
    public static void insert(int index, byte[] values, long value) {
        byte[] bt = NumberUtil.toBytes(value);
        System.arraycopy(bt, 0, values, index, 8);
    }

    /**
     * 将字节转换成整型
     *
     * @param value 字节类型值
     * @return
     */
    public static int byteToInt(byte value) {
        if (value < 0) {
            return value + 256;
        }
        return value;
    }

    /**
     *
     * 除法，返回2位精度的百分比数字 80.23
     *
     * @param num
     * @param div
     * @return
     * @date 2015年10月17日 上午8:12:30
     */
    public static double divideRate(Object num, Object div) {
        double a = NumberUtil.parseDouble(num);
        double b = NumberUtil.parseDouble(div);
        if (b != 0) {
            return Double.parseDouble(NumberUtil.format(a * 100 / b, 2));//精确到小数点2位
        } else {
            return 0.00;
        }

    }

    /**
     *
     * 除法，返回2位精度的百分比数字 80.23
     *
     * @param num
     * @param div
     * @return
     * @date 2015年10月17日 上午8:12:30
     */
    public static String divideRateStr(Object num, Object div) {
        double a = NumberUtil.parseDouble(num);
        double b = NumberUtil.parseDouble(div);
        if (b != 0) {
            return NumberUtil.format(a * 100 / b, 2);//精确到小数点2位
        } else {
            return "0.00";
        }

    }

    /**
     *
     * 除法，返回2位精度的数字 0.23
     *
     * @param num
     * @param div
     * @return
     * @version v1.0
     * @author 徐高波
     * @date 2015年12月1日 下午1:00:26
     */
    public static double divide(Object num, Object div) {
        double a = NumberUtil.parseDouble(num);
        double b = NumberUtil.parseDouble(div);
        if (b != 0) {
            return Double.parseDouble(NumberUtil.format(a / b, 2));//精确到小数点2位
        } else {
            return 0.00;
        }

    }

    /**
     *
     * 除法
     *
     * @param num
     * @param div
     * @return
     * @version v1.0
     * @author 徐高波
     * @date 2015年12月1日 下午1:00:26
     */
    public static double divideA(Object num, Object div) {
        double a = NumberUtil.parseDouble(num);
        double b = NumberUtil.parseDouble(div);
        if (b != 0) {
            return a / b;
        } else {
            return 0.0;
        }

    }

    /**
     *
     * 获取String类型数字
     *
     * @param obj
     * @return
     * @version v1.0
     * @author 徐高波
     * @date 2015年12月4日 上午11:00:14
     */
    public static String formatInteger(Integer obj) {
        if (obj == null) {
            return "0";
        } else {
            return obj.toString();
        }

    }

    /**
     * 使用java正则表达式去掉多余的.与0 主要是处理“1.0”显示在为“1”
     *
     * @param f
     * @return
     */
    public static String FormatFloat(Float f) {
        if (f == null) {
            return "0";
        }
        String s = f.toString();
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }

    /**
     * 使用java正则表达式去掉多余的.与0 主要是处理“1.0”显示在为“1”
     *
     * @param f
     * @return
     */
    public static String formatStr(String s) {
        if (s == null) {
            return "0";
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }

    /**
     * 使用java正则表达式去掉多余的.与0 主要是处理“1.0”显示在为“1”
     *
     * @param f
     * @return
     */
    public static Double formatDouble(String s) {
        if (s == null) {
            return Double.valueOf("0");
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }

        return Double.valueOf(s);
    }

    /**
     * 使用java正则表达式去掉多余的.与0 主要是处理“1.0”显示在为“1”
     *
     * @param f
     * @return
     */
    public static String FormatDoubleZero(Double f) {
        if (f == null) {
            return "0";
        }
        String s = f.toString();
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }

    /**
     * 转换Double数字成概率，例如0.25696,转换成25.7,页面展示时，需要自行拼接% add by liuhj 20161215
     *
     * @param value 原始值
     * @param precision 精度，小数点后多少位
     * @param isIncludePostfix:是否包含%后缀
     * @return 格式化后的字符串
     */
    public static Object formatDoubleToRate(Double value, Integer precision,
            boolean isIncludePostfix) {
        Double first = NumberUtil.formatDouble(value * 100, precision + 1);
        Object end = NumberUtil.formatDouble(first, precision);
        return isIncludePostfix ? (end + "%") : end;
    }

    /**
     * 将数字转换成大写
     *
     * @author kyq
     * @version
     * @date 2017/4/26 13:50
     * @param number 数字
     */
    public static String number2Chinese(String number) {
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿"};
        String result = "";
        int n = number.length();
        for (int i = 0; i < n; i++) {
            int num = number.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                if (result == "" || !(("零".equals(result.substring(result.length() - 1))) && "零".equals(s1[num]))) {
                    result += s1[num];
                }
            }
        }
        System.out.println("----------------");
        System.out.println(result);
        return result;
    }

    
    static String convertHz(long hz) {
        if (hz <= 0) {
            return "";
        }

        if (hz < 1000) {
            return hz + " MHz";
        } else {
            return cn.com.jingcloud.utils.NumberUtil.divide(hz, 1000) + " GHz";
        }
    }
    public static void main(String[] args) {
////        System.out.println(isDigit("66"));
        String f = divideRateStr(12, 100);
        System.out.println(f);


        String  ss = convertHz(2260);
        System.out.println(ss);
    }
}
