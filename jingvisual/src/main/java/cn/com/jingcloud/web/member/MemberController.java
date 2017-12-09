/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web.member;

import cn.com.jingcloud.domain.entity.shiro.User;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author ly
 */
@ApiIgnore
@Controller
@RequestMapping(value = "/member")
public class MemberController {
    // 拦截/index.htm 方法为GET的请求

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/member/index");
        return view;
    }

    @RequestMapping("/hello")
    @ResponseBody
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
