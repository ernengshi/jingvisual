/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.utils.BlogProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author ly
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogPropertiesTest {

    @Autowired
    private BlogProperties blogProperties;

    @Test
    public void getHello() throws Exception {
//        Assert.assertEquals(blogProperties.getName(), "程序猿DD");
//        Assert.assertEquals(blogProperties.getTitle(), "Spring Boot教程");
        System.out.println("######## " + blogProperties.getName());
    }
}
