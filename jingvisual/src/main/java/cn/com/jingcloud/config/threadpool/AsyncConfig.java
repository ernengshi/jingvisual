/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.threadpool;

/**
 * https://github.com/himanshuvirmani/spring-boot-base-template/blob/master/src/main/java/com/springboot/demo/config/AsyncConfiguration.java
 * http://www.concretepage.com/spring/example_threadpooltaskexecutor_spring
 *
 * @author liyong
 */
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

@Configuration
//@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Autowired
    @Qualifier("springExecutor")
    private Executor springExecutor;

    @Override
    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(10);
//        executor.setThreadNamePrefix("Jingvisual-Executor-Async-");
//        executor.initialize();
        return springExecutor;
    }

    /**
     * 此时异常处理 参见 http://rensanning.iteye.com/blog/2360749
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
