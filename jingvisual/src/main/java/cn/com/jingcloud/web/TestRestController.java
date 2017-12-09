/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @RestController来处理请求，所以返回的内容为json对象
 *
 *
 *
 * @Controller：修饰class，用来创建处理http请求的对象
 * @RestController：Spring4之后加入的注解，原来在@Controller中返回json需要
 * @ResponseBody来配合，如果直接用@RestController替代@Controller就不需要再配置
 * @ResponseBody，默认返回json格式。
 * @RequestMapping：配置url映射
 */
@ApiIgnore
@RestController
@Api(value = "测试")
public class TestRestController {

    @ApiOperation(value = "测试", notes = "测试1")
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String index(@RequestParam Integer a, @RequestParam Integer b) {
        return "" + (a + b);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Spring Boot 1.5.x中引入的一个新的控制端点：/loggers
     *
     * 在应用主类中添加一个接口用来测试日志级别的变化
     */
    @RequestMapping(value = "/logtest", method = RequestMethod.GET)
    public String testLogLevel() {
        logger.debug("Logger Level ：DEBUG");
        logger.info("Logger Level ：INFO");
        logger.error("Logger Level ：ERROR");
        return "";
    }
}
