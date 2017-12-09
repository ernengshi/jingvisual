/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.restfulapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger-ui.html
 * 文档生成器
 *
 * @Configuration可理解为用spring的时候xml里面的<beans>标签
 *
 * @Bean可理解为用spring的时候xml里面的<bean>标签
 *
 * 常将用于存放配置信息的类的类名以 “Config” 结尾
 *
 * @author liyong
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.com.jingcloud.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Jingvisual RESTful APIs")
                .description("可视化平台：liyong@jingcloud.com")
                .termsOfServiceUrl("liyong@jingcloud.com")
                .contact("李勇")
                .version("1.0")
                .build();
    }
}
