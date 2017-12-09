/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用工具类
 *
 * @author liong
 */
public class Utils {
    
    private final static Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * null or "" or " "
     */
    public static boolean isNullOrEmpty(String param) {
        return ((param == null) || (param.trim().length() == 0));
    }

    /**
     * null or empty
     */
    public static boolean isNullOrEmpty(List list) {
        return (null == list || list.isEmpty());
    }

    /**
     * 给指定字符串编码
     *
     * @param value
     * @charset UTF-8
     */
    public static String encodeString(String value) {
        try {
            return URLEncoder.encode(value, Charsets.UTF_8_NAME);
        } catch (Exception e) {
            LOG.warn("Unable to encode: " + value, e);
        }
        return value;
    }

    /**
     * 给指定字符串解码
     *
     * @param value
     * @charset UTF-8
     */
    public static String decodeString(String value) {
        try {
            return URLDecoder.decode(value, Charsets.UTF_8_NAME);
        } catch (Exception e) {
            LOG.warn("Unable to decode: " + value, e);
        }
        return value;
    }

    /**
     * list转换为string
     */
    public static String listToString(List<String> list) {
        return org.apache.commons.lang3.StringUtils.join(list, ",");
    }

    /**
     * 将byte转换成可识别的单位
     *
     * @param byte
     */
    protected static final long KB = 1024;
    protected static final long MB = 1024 * KB;
    protected static final long GB = 1024 * MB;
    protected static final long TB = 1024 * GB;
    
    public static String toReadableSize(long bytes) {
        if (bytes <= KB && bytes >= 0) {
            return Long.toString(bytes) + " bytes";
        } else if (bytes <= MB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f KB", (float) bytes / (float) KB);
            return builder.toString();
        } else if (bytes <= GB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f MB", (float) bytes / (float) MB);
            return builder.toString();
        } else if (bytes <= TB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f GB", (float) bytes / (float) GB);
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.4f TB", (float) bytes / (float) TB);
            return builder.toString();
        }
    }
    
    public static String concat(Object... argv) {
        StringBuilder buf = new StringBuilder();
        for (Object s : argv) {
            buf.append(s);
        }
        return buf.toString();
    }
    
    public static void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ex) {
            LOG.error("sleep failed" + ex.getMessage());
        }
        
    }
}
