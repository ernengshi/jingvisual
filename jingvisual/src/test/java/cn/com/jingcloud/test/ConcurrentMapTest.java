/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 
 * http://blog.csdn.net/kkgbn/article/details/52163306
 * 
 * https://my.oschina.net/mononite/blog/144329
 * 
 * 
 * 
 * ConcurrentHashMap通常只被看做并发效率更高的Map，
 * 用来替换其他线程安全的Map容器，比如Hashtable和Collections.synchronizedMap。
 * 实际上，线程安全的容器，特别是Map，应用场景没有想象中的多， 很多情况下一个业务会涉及容器的多个操作，即复合操作，并发执行时，
 * 线程安全的容器只能保证自身的数据不被破坏，但无法保证业务的行为是否正确。
 *
 * 举个例子：统计文本中单词出现的次数，把单词出现的次数记录到一个Map中，代码如下：
 * 如果多个线程并发调用这个increase()方法，increase()的实现就是错误的，
 * 因为多个线程用相同的word调用时，很可能会覆盖相互的结果，造成记录的次数比实际出现的次数少。
 *
 *
 * 除了用锁解决这个问题，另外一个选择是使用ConcurrentMap接口定义的方法：
 *
 * public interface ConcurrentMap<K, V> extends Map<K, V> { V putIfAbsent(K
 * key,V value); boolean remove(Object key, Object value); boolean replace(K
 * key, VoldValue, V newValue); V replace(K key, V value); }
 *
 * 这是个被很多人忽略的接口，也经常见有人错误地使用这个接口。 ConcurrentMap接口定义了几个基于 CAS（Compare and
 * Set）操作，很简单，但非常有用，下面的代码用ConcurrentMap解决上面问题：
 *
 * @author liyong
 */
public class ConcurrentMapTest {

    ConcurrentMap<String, Set> map = new ConcurrentHashMap<String, Set>();

    /**
     * https://stackoverflow.com/questions/3752194/should-you-check-if-the-map-containskey-before-using-concurrentmaps-putifabsent
     *
     * @param name
     */
    public void demo(String name) {
        Set set = map.get(name);
        if (set == null) {
            final Set value = new HashSet();
            set = map.putIfAbsent(name, value);
            if (set == null) {
                set = value;
            }
        }
    }

//    https://my.oschina.net/mononite/blog/144329
    private final ConcurrentMap<String, Long> wordCounts = new ConcurrentHashMap<>();

    public long increase(String word) {
        Long oldValue, newValue;
        while (true) {
            oldValue = wordCounts.get(word);
            if (oldValue == null) {
                // Add the word firstly, initial the value as 1
                newValue = 1L;
                if (wordCounts.putIfAbsent(word, newValue) == null) {
                    break;
                }
            } else {
                newValue = oldValue + 1;
                if (wordCounts.replace(word, oldValue, newValue)) {
                    break;
                }
            }
        }
        return newValue;
    }

    private final ConcurrentMap<String, AtomicLong> wordCounts2 = new ConcurrentHashMap<>();

    public long increase2(String word) {
        AtomicLong number = wordCounts2.get(word);
        if (number == null) {
            AtomicLong newNumber = new AtomicLong(0);
            number = wordCounts2.putIfAbsent(word, newNumber);
            if (number == null) {
                number = newNumber;
            }
        }
        return number.incrementAndGet();
    }

    private final ConcurrentMap<String, Future<ExpensiveObj>> cache = new ConcurrentHashMap<>();

    public Object get(final String key) {
        Future<ExpensiveObj> future = cache.get(key);
        if (future == null) {
            Callable<ExpensiveObj> callable = new Callable<ExpensiveObj>() {
                @Override
                public ExpensiveObj call() throws Exception {
                    return new ExpensiveObj(key);
                }
            };
            FutureTask<ExpensiveObj> task = new FutureTask<>(callable);

            future = cache.putIfAbsent(key, task);
            if (future == null) {
                future = task;
                task.run();
            }
        }

        try {
            return future.get();
        } catch (Exception e) {
            cache.remove(key);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] s) {
        Map<String, String> whoLetDogsOut = new ConcurrentHashMap<>();
        System.out.println(whoLetDogsOut.computeIfAbsent("snoop", k -> f(k)));;
        System.out.println(whoLetDogsOut.computeIfAbsent("snoop", (String k) -> f(k)));
    }

    static String f(String s) {
        System.out.println("creating a value for \"" + s + '"');
        return s;
    }
}
