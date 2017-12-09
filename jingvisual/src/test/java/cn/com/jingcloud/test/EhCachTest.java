/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.p.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import cn.com.jingcloud.domain.dao.p.UserRepository;

/**
 * cache测试 在Spring Boot中通过@EnableCaching注解自动化配置合适的缓存管理器（CacheManager），Spring
 * Boot根据下面的顺序去侦测缓存提供者：
 *
 * Generic， JCache (JSR-107) ，EhCache 2.x， Hazelcast， Infinispan， Redis， Guava，
 * Simple
 * 除了按顺序侦测外，我们也可以通过配置属性spring.cache.type来强制指定。我们可以通过debug调试查看cacheManager对象的实例来判断当前使用了什么缓存。
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EhCachTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void before() {
        userRepository.deleteAll();
        userRepository.save(new User("AAA", 10));
    }

    @Test
    public void test() throws Exception {
        User u1 = userRepository.findByName("AAA");
        System.out.println("第一次查询：" + u1.getAge());
        User u2 = userRepository.findByName("AAA");
        System.out.println("第二次查询：" + u2.getAge());
        u2.setAge(20);
        userRepository.save(u2);
        User u3 = userRepository.findByName("AAA");
        System.out.println("第三次查询：" + u3.getAge());
    }
}
