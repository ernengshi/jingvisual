/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.upload;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * http://www.jb51.net/article/109122.htm 文件上传配置
 * http://blog.csdn.net/shi0299/article/details/69525848
 *
 * @author liyong
 */
@Configuration
public class FileUploadConfiguration {

    /**
     * 或者在application.properties文件中设置文件大小
     *
     * Spring Boot 1.3.x或者之前
     *
     * multipart.maxFileSize=100Mb multipart.maxRequestSize=1000Mb
     *
     * Spring Boot 1.4.x或者之后
     *
     * spring.http.multipart.maxFileSize=100Mb
     * spring.http.multipart.maxRequestSize=1000Mb
     *
     * #默认支持文件上传. 
     * #spring.http.multipart.enabled=true 
     * #支持文件写入磁盘.
     * #spring.http.multipart.file-size-threshold=0 
     * # 上传文件的临时目录
     * #spring.http.multipart.location= 
     * # 最大支持文件大小
     * spring.http.multipart.max-file-size=4Mb 
     * # 最大支持请求大小
     * spring.http.multipart.max-request-size=10Mb
     *
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制 ,超出设置页面会抛出异常信息，
        // 这样在文件上传的地方就需要进行异常信息的处理了;
        factory.setMaxFileSize("256MB"); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("512MB");
        // Sets the directory location where files will be stored.
        // factory.setLocation("路径地址");
        return factory.createMultipartConfig();
    }
}
