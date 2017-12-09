/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.p.RedisUser;
import cn.com.jingcloud.utils.BasicDataUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * Redis
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //BasicDataUtils是在RedisUtils上的封装用于对象的存储
    @Autowired
    BasicDataUtils BasicDataUtils;

    @Test
    public void testString() throws Exception {
        // 保存字符串
        stringRedisTemplate.opsForValue().set("aaa", "111");
        System.out.println("########################" + stringRedisTemplate.opsForValue().get("aaa"));
        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    public static final String key = "test";

    @Test
    public void setObj() throws Exception {
        // 保存对象
        RedisUser user = new RedisUser("超人", 20, 10);
        BasicDataUtils.setRedis(key, user);
    }

    @Test
    public void getObj() throws Exception {
        // 保存对象
       RedisUser user =  BasicDataUtils.getRedis(key, RedisUser.class);
        System.out.println(user.getName().equals("超人"));
    }
}
