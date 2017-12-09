/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.utils.index;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author liyong
 */
public class IndexConstants {

    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
    public static final Map<String, IndexFileItem> IndexFileMap = new ConcurrentHashMap<>();
}
