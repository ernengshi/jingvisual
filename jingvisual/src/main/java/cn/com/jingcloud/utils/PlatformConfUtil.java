package cn.com.jingcloud.utils;

import cn.com.jingcloud.utils.constant.JingVisualConstant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author liyong
 */
public class PlatformConfUtil {

    private volatile static PlatformConfUtil configUtil;
    private static final Map<String, String> MAP = new LinkedHashMap<String, String>();
    public static final String JINGVISUAL_PLATFORM_CONF_PATH = JingVisualConstant.FILE_PATH + "conf" + Platforms.FILE_PATH_SEPARATOR + "platform.conf";
    //platform name
    public static final String JINGVISUAL_PLATFORM_IAAS = "IAAS";
    public static final String JINGVISUAL_PLATFORM_DAAS = "DAAS";
    public static final String CHANGE_MOMORY_TYPE_ADD = "add";
    public static final String CHANGE_MOMORY_TYPE_UPDATE = "update";
    public static final String CHANGE_MOMORY_TYPE_GET = "get";
    public static final String CHANGE_MOMORY_TYPE_DELETE = "delete";
    private static final Logger S_LOGGER = Logger.getLogger(PlatformConfUtil.class.getName());

    private PlatformConfUtil() {
        try {
            readConfigFileToMap(JINGVISUAL_PLATFORM_CONF_PATH);
        } catch (Exception ex) {
            S_LOGGER.error("readPlatformConfFileToMap: " + ex.getMessage());
        }
    }

    public static PlatformConfUtil instance() {
        if (configUtil == null) {
            synchronized (PlatformConfUtil.class) {
                if (configUtil == null) {
                    configUtil = new PlatformConfUtil();
                }
            }
        }
        return configUtil;
    }

    private void readConfigFileToMap(String file_path) throws Exception {
        File file = new File(file_path);
        if (!file.exists()) {
            FileUtils.touch(file);
        }

        List<String> list = IOUtils.readLines(new FileInputStream(file), Charsets.UTF_8);
        for (String str : list) {
            if (!Utils.isNullOrEmpty(str)) {
                String[] arr = null;
                arr = StringUtils.split(str, "=");
                if (arr != null && arr.length == 2) {
                    MAP.put(arr[0], arr[1]);
                }
            }
        }
    }

    private void changeConfigFile() throws IOException {
        List<String> strs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : MAP.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String strLine = "";
            strLine = key + "=" + value;
            if (!Utils.isNullOrEmpty(strLine)) {
                strs.add(strLine);
            }
        }
        OutputStream out = new FileOutputStream(JINGVISUAL_PLATFORM_CONF_PATH);
        IOUtils.writeLines(strs, Platforms.LINE_SEPARATOR, out, Charsets.UTF_8);
        IOUtils.closeQuietly(out);
    }

    /**
     * @param list<String> eg: cloud.student.vm.cpu.number=2
     */
    private void changeMeory(List<String> list, String type) {
        for (String str : list) {
            if (!Utils.isNullOrEmpty(str)) {
                String[] arr = null;
                arr = StringUtils.split(str, "=");
                if (null != arr && arr.length == 2) {
                    String name = arr[0];
                    String value = arr[1];
                    if (!Utils.isNullOrEmpty(name)
                            && !Utils.isNullOrEmpty(value)) {//add or update or delete

                        if (CHANGE_MOMORY_TYPE_UPDATE.equals(type)
                                && MAP.containsKey(name)
                                && !MAP.containsValue(value)) {//update
                            MAP.put(name, value);
                        } else if (CHANGE_MOMORY_TYPE_ADD.equals(type)
                                && !MAP.containsKey(name)
                                && !MAP.containsValue(value)) {//add
                            MAP.put(name, value);
                        } else if (CHANGE_MOMORY_TYPE_DELETE.equals(type)
                                && MAP.containsKey(name)
                                && MAP.containsValue(value)) {//delete
                            MAP.remove(name, value);
                        }
                    }
                }
            }
        }
    }

    public Map<String, String> getMap() {
        return MAP;
    }

    /**
     * update
     *
     * @param list<String> eg: cloud.student.vm.cpu.number=2
     */
    public void updateConfig(List<String> list) throws Exception {
        changeMeory(list, CHANGE_MOMORY_TYPE_UPDATE);
        changeConfigFile();
    }

    /**
     * add
     *
     * @param list
     * @throws Exception
     */
    public void addConfig(List<String> list) throws Exception {
        changeMeory(list, CHANGE_MOMORY_TYPE_ADD);
        changeConfigFile();
    }

    /**
     * get
     *
     * @param key
     * @return
     */
    public String getConfig(String key) {
        return MAP.get(key);
    }

    /**
     * delete
     *
     * @param list
     * @throws Exception
     */
    public void deleteConfig(List<String> list) throws Exception {
        changeMeory(list, CHANGE_MOMORY_TYPE_DELETE);
        changeConfigFile();
    }

    public static void main(String[] args) throws Exception {
        PlatformConfUtil util = PlatformConfUtil.instance();
        List<String> list = new ArrayList<>();
//add
//        list.add("IAAS=192.168.211.252,8096");
//        list.add("DAAS=192.168.13.51,80");
//        util.addConfig(list);
//update
//        list.add("DAAS=192.168.13.51,81");
//        util.updateConfig(list);
//delete
//        list.add("DAAS=192.168.13.51,81");
//        util.deleteConfig(list);
        String value = util.getConfig(JINGVISUAL_PLATFORM_IAAS);
        System.out.println(value);
    }
}
