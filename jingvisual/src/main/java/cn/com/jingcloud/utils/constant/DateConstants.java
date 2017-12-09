/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils.constant;

import java.text.SimpleDateFormat;


/**
 *
 * @author liyong
 */
public class DateConstants {

    // 统一日期时间格式
    public static final String TIMEZONE = "GMT+08:00";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORM = "yyyyMMdd";
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    public static final String DATE_Minute_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_DETAIL = "yyyyMMddHHmmssSSS";
    public final static SimpleDateFormat DATE_FORMATER = new SimpleDateFormat(DATE_FORMAT);
    public final static SimpleDateFormat DATE_FORMA = new SimpleDateFormat(DATE_FORM);
    public final static SimpleDateFormat DATE_FORMATER_CN = new SimpleDateFormat(DATE_FORMAT_CN);
    public final static SimpleDateFormat DATE_TIME_FORMATER = new SimpleDateFormat(DATE_TIME_FORMAT);
    public final static SimpleDateFormat DATE_TIME_DETAIL_FORMATER = new SimpleDateFormat(DATE_TIME_FORMAT_DETAIL);
}
