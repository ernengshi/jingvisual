/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.threadpool;

import cn.com.jingcloud.utils.thread.NamedThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author liyong
 */
@Configuration
public class SpringThreadPoolConfig {

    private final static Logger LOG = LoggerFactory.getLogger(SpringThreadPoolConfig.class);

    @Value("${Thread-pool-task-executor.thread-name-prefix}")
    private String threadNnamePrefix;

    @Value("${Thread-pool-task-executor.core-pool-size}")
    private int corePoolSize;
    @Value("${Thread-pool-task-executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${Thread-pool-task-executor.queue-capacity}")
    private int queueCapacity;

    /**
     * spring 的线程池
     *
     * @return
     */
    @Bean(name = "springExecutor")
    public Executor springExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNnamePrefix);

//corePoolSize：线程池维护线程的最少数量
//maxPoolsize：线程池维护线程的最大数量，缺省：Integer.MAX_VALUE
//queue-capacity：当最小的线程数已经被占用满后，新的任务会被放进queue里面，当这个queue的capacity也被占满之后，pool里面会创建新线程处理这个任务，直到总线程数达到了max size，这时系统会拒绝这个任务并抛出TaskRejectedException异常（缺省配置的情况下，可以通过rejection-policy来决定如何处理这种情况）。缺省值为：Integer.MAX_VALUE
//keep-alive：超过core size的那些线程，任务完成后，再经过这个时长（秒）会被结束掉
//rejection-policy：当pool已经达到max size的时候，如何处理新任务
//ABORT（缺省）：抛出TaskRejectedException异常，然后不执行
//DISCARD：不执行，也不抛出异常
//DISCARD_OLDEST：丢弃queue中最旧的那个任务
//CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务  
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(new NamedThreadFactory("JingVisual-ThreadFactory-"));

    /**
     * java自定义线程池
     *
     * @return
     */
    @Bean(name = "javaExecutorService")
    public ExecutorService javaExecutorService() {
        return EXECUTOR_SERVICE;
    }

    @PreDestroy
    public void stopService() {
        LOG.info("thread pool begin to close");
        EXECUTOR_SERVICE.shutdown();
        try {
            EXECUTOR_SERVICE.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOG.error("closed ExecutorService error!!!" + ex.getMessage());
        }
        LOG.info("thread pool already closed");
    }
}
