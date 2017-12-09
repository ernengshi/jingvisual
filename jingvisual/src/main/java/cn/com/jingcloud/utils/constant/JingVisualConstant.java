/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils.constant;

import cn.com.jingcloud.utils.Platforms;

/**
 *
 * @author liyong
 */
public class JingVisualConstant {

    public static final int DAY_SIZE = 24;
    public static final int WEEK_SIZE = DAY_SIZE * 7;
    public static final int MONTH_SIZE = DAY_SIZE * 30;
    public static final int SEASON_SIZE = MONTH_SIZE * 3;
    public static final int YEAR_SIZE = 365 * DAY_SIZE;
    public static String FILE_PATH = "D:\\aa\\";

    static {
        if (Platforms.IS_WINDOWS) {
            FILE_PATH = "D:\\aa\\";
        } else if (Platforms.IS_LINUX) {
            FILE_PATH = "/opt/";
        }
    }
    public static final String INDEX_FILE_PATH = FILE_PATH + "index" + Platforms.FILE_PATH_SEPARATOR;
    public static final String ZONE_FILE_PATH = INDEX_FILE_PATH + "zone" + Platforms.FILE_PATH_SEPARATOR;
    public static final String HOST_FILE_PATH = INDEX_FILE_PATH + "host" + Platforms.FILE_PATH_SEPARATOR;
    public static final String VM_FILE_PATH = INDEX_FILE_PATH + "vm" + Platforms.FILE_PATH_SEPARATOR;
    public static final String TC_FILE_PATH = INDEX_FILE_PATH + "tc" + Platforms.FILE_PATH_SEPARATOR;
    
    public static final String JINGVISUAL_PLATFORM_CONF_PATH = FILE_PATH + "conf" + Platforms.FILE_PATH_SEPARATOR + "platform.conf";
    public static final String JINGVISUAL_ON_DUTY_CONF_PATH = JingVisualConstant.FILE_PATH + "conf" + Platforms.FILE_PATH_SEPARATOR + "onduty.conf";
    
    
    public static final String FILE_PATH_SUFFIX = ".index";
    public static final String UNDER_LINE = "_";
    public static final int PAGE_SIZE = 2;

    public static final int IAAS_PORT = 8096;
    public static final int DAAS_PORT = 80;
    public static final int IAAS_TYPE = 1;
    public static final int DAAS_TYPE = 2;

//    public static final String DAAS_TOKEN = "external_system_token";
}
