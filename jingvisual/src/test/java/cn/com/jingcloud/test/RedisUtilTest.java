package cn.com.jingcloud.test;

import cn.com.jingcloud.BaseApplicationTests;
import cn.com.jingcloud.config.redis.RedisUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Redis工具类测试
 */
public class RedisUtilTest extends BaseApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void getIncreValue() throws Exception {
        long value = redisUtils.getincrementValue("test1111");
        System.err.println("value====>" + value);
    }
}
