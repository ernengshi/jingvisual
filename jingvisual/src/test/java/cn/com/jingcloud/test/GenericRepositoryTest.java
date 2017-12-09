/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.entity.generic.User;
//import cn.com.jingcloud.service.ThreadPoolService;
import cn.com.jingcloud.utils.repository.GenericRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GenericRepositoryTest {

    @Autowired
    @Qualifier("primaryGenericRepository")
    GenericRepository genericRepository;

    @Before
    public void setUp() {
        genericRepository.tryUpdate("delete from user", Collections.emptyMap());
    }

    public int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private long create() {
        User u = new User();
        u.setAge(11);
        u.setName("test");
        long id = genericRepository.create(u);
        return id;
    }

//    @Autowired
//    ThreadPoolService threadPoolService;
    @Autowired
    @Qualifier("javaExecutorService")
    ExecutorService javaExecutorService;

    class MyThreadTest extends Thread {

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    User u = new User();
                    u.setAge(11);
                    u.setName("李四");
                    long id = genericRepository.create(u);
                    System.out.println(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                latch.countDown(); //state值减1
            }
        }
    }

    private int taskCount = 100;
//    CountDownLatch latch = new CountDownLatch(taskCount); //新建倒计时计数器，设置state为taskCount变量值

    /**
     * 并发有限
     */
    @Test
    public void createThread() {
//        ExecutorService service = threadPoolService.getExecutors();
//        for (int i = 0; i < 100; i++) {
//            service.execute(new Runnable() {
//                @Override
//                public void run() {
//                    User u = new User();
//                    u.setAge(11);
//                    u.setName("李四");
//                    long id = genericRepository.create(u);
//                    System.out.println(id);
//                }
//            });
//        }
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(taskCount);
        for (int i = 0; i < taskCount; i++) {
            // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
            Thread t = new MyThreadTest();
            // 将线程放入池中进行执行
            pool.execute(t);
//            t.start();
        }
        try {
            //等待直到state值为0，再继续往下执行
//            latch.await();
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createOne() {
        User u = new User();
        u.setAge(11);
        u.setName("李四");
        long id = genericRepository.create(u);

    }

    @Test
    public void basicCreateUpdateDelete() throws Exception {
        User u = new User();
        u.setAge(12);
        u.setName("test");
        long id = genericRepository.create(u);
        System.out.println("###########" + id);
        u.setId(id);
        u.setName("test2");
        genericRepository.update(u);
        User user1 = genericRepository.find(id, User.class);
        Assert.assertEquals("test2", user1.getName());
        genericRepository.delete(u);
        User user2 = genericRepository.find(id, User.class);
        Assert.assertNull(user2);
    }

    @Transactional
    @Rollback(true)
    @Test
    public void testFindFirst() {
        long id = create();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        User u1 = genericRepository.findFirst(User.class, "select * from user where id=:id ", paramMap);

        User u2 = genericRepository.findFirst(User.class, "select * from user where id=? ", new Object[]{id});
        System.out.println(u1.getId() + " " + u2.getId());
        Assert.assertEquals(u1.getId(), u2.getId());

    }

    @Transactional
    @Rollback(true)
    @Test
    public void testPage() {
        int size = 20;
        int pageNo = 1;
        int pageSize = 10;
        for (int i = 0; i < 20; i++) {
            create();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 0);
        List<User> list1 = genericRepository.page(User.class, "select * from user where id > :id", pageNo, pageSize, paramMap);

        List<User> list2 = genericRepository.page(User.class, "select * from user where id > ?", pageNo, pageSize, new Object[]{0});
        int index = randInt(0, size - 1);
        System.out.println("random: " + index);
        long u1 = list1.get(index).getId();
        long u2 = list2.get(index).getId();
        System.out.println(u1 + "  " + u2);
        Assert.assertEquals(u2, u2);
    }

    @Transactional
    @Rollback(true)
    @Test
    public void testCount() {
        for (int i = 0; i < 20; i++) {
            create();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 0);
        int count1 = genericRepository.count("select count(*) from user where id > :id", paramMap);
        int count2 = genericRepository.count("select count(*) from user where id > ?", new Object[]{0});
        System.out.println(count1 + "   " + count2);

        Assert.assertEquals(count1, count2);
    }
//    list

    @Transactional
    @Rollback(true)
    @Test
    public void testList() {
        int size = 20;
        for (int i = 0; i < size; i++) {
            create();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 0);
        List<User> list1 = genericRepository.list(User.class, "select * from user where id > :id", paramMap);
        List<User> list2 = genericRepository.list(User.class, "select * from user where id > ?", new Object[]{0});
        int index = randInt(0, size - 1);
        System.out.println("random: " + index);
        long u1 = list1.get(index).getId();
        long u2 = list2.get(index).getId();
        System.out.println(u1 + "  " + u2);
        Assert.assertEquals(u2, u2);
    }

    @Transactional
    @Rollback(true)
    @Test
    public void testListId() {
        int size = 20;
        for (int i = 0; i < size; i++) {
            create();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 0);
        List<Long> list1 = genericRepository.listId("select * from user where id > :id", paramMap);
        List<Long> list2 = genericRepository.listId("select * from user where id > ?", new Object[]{0});
        int index = randInt(0, size - 1);
        System.out.println("random: " + index);
        long id1 = list1.get(index);
        long id2 = list2.get(index);
        System.out.println(id1 + "  " + id2);
        Assert.assertEquals(id2, id2);

    }
//tryUpdate

    /**
     * 原始sql 处理 add/update/delete
     */
    @Transactional
    @Rollback(true)
    @Test
    public void testTryUpdate() {
        long id = create();

        long nextId = id + 1;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", nextId);
        paramMap.put("name", "test1");
        paramMap.put("age", 11);
        genericRepository.tryUpdate("insert into user (id,name,age) values (:id,:name,:age)", paramMap);

        User user3 = genericRepository.find(nextId, User.class);
        System.out.println(nextId + "__________" + user3.getId());
        Assert.assertEquals(nextId, user3.getId());

        genericRepository.tryUpdate("update user set name=?,age=? where id=?", new Object[]{"test2", 12, nextId});
        User user1 = genericRepository.find(nextId, User.class);
        System.out.println(user1.getAge() + "__________" + user1.getName());
        Assert.assertEquals("test2", user1.getName());

        User u = new User();
        u.setId(nextId);
        genericRepository.tryUpdate("delete from user where id=:id", u);

        User user2 = genericRepository.find(nextId, User.class);
        Assert.assertNull(user2);
    }
}
