package cn.com.jingcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 在Spring Boot的主类中加入@EnableScheduling注解，启用定时任务的配置</br>
 * 在Spring Boot的主类中加入@EnableAsync注解，启用异步任务的配置</br>
 * 在Spring Boot的主类中加入@EnableCaching注解，启用spring缓存</br>
 *
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class JingVisualApplication {

    /**
     * 钩子 http://www.importnew.com/22765.html
     */
    private static void releaseResource() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Execute Hook...................");
            }
        }));
    }

//    https://stackoverflow.com/questions/30431776/using-scheduled-and-enablescheduling-but-gives-nosuchbeandefinitionexception
//    @Bean
//    public Executor taskExecutor() {
//        return new SimpleAsyncTaskExecutor();
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        return new ConcurrentTaskScheduler(); //single threaded by default
//    }
    public static void main(String[] args) {
//        releaseResource();
        SpringApplication.run(JingVisualApplication.class, args);
    }

}
