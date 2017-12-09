/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

/**
 *
 * @author liyong
 */
public interface LocaleMessageSourceService {

    /**
     * @param code ：对应messages配置的key.
     * @return
     */
    String getMessage(String code);

    /**
     *
     * @param code ：对应messages配置的key.
     * @param args : 数组参数.
     * @return
     */
    String getMessage(String code, Object[] args);

    /**
     *
     * @param code ：对应messages配置的key.
     * @param args : 数组参数.
     * @param defaultMessage : 没有设置key的时候的默认值.
     * @return
     */
    String getMessage(String code, Object[] args, String defaultMessage);

}
