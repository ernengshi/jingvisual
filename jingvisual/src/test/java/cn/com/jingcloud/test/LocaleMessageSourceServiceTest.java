/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.service.LocaleMessageSourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * java 后台代码 读取国际化配置文件信息
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocaleMessageSourceServiceTest {

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @Test
    public void test() throws Exception {
        String msg3 = localeMessageSourceService.getMessage("welcome");
        System.out.println("#########" + msg3);
    }
}
