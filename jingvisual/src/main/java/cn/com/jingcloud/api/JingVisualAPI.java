/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.api;

import cn.com.jingcloud.domain.entity.shiro.User;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方调用的api basic auth校验
 *
 * @author liyong
 */
@RestController
@RequestMapping("/api")
public class JingVisualAPI {

    /**
     *
     *
     * @param request
     * @return
     */
    @RequestMapping("/hello")
    public User hello(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Enumeration<String> headers = httpRequest.getHeaderNames();
        while (headers.hasMoreElements()) {//以此取出头信息
            String headerName = (String) headers.nextElement();
            String headerValue = httpRequest.getHeader(headerName);//取出头信息内容
        }
        //return "hello";
        User user = new User("shiro", "123456");
        return user;
    }
}
