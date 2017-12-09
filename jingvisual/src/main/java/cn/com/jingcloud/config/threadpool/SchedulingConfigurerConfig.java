/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * https://stackoverflow.com/questions/29796651/what-is-the-default-scheduler-pool-size-in-spring-boot
 *
 * @Scheduled methods share a single thread by default. It is possible to
 * override this behavior by defining a @Configuration such as this:
 * @author liyong
 */
@Configuration
public class SchedulingConfigurerConfig implements SchedulingConfigurer {

    @Value("${thread-pool.task-scheduler.thread-name-prefix}")
    private String threadNnamePrefix;

    @Value("${thread-pool.task-scheduler.pool-size}")
    private int poolSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix(threadNnamePrefix);
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
