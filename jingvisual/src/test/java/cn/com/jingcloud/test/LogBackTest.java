/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ly
 */
public class LogBackTest {

    private final static Logger logger = LoggerFactory.getLogger(LogBackTest.class);

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            logger.info("logback info 成功了");
//            logger.error("logback error 成功了");
//            logger.debug("logback debug 成功了");
//            logger.warn("logback warn 成功了");
//            logger.trace("logback trace 成功了");
        }
    }
}
