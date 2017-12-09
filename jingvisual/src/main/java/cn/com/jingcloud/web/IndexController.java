/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.shiro.Role;
import cn.com.jingcloud.domain.entity.shiro.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author liyong
 */
@ApiIgnore
@Controller

public class IndexController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/thymeleaf")
    public String index(ModelMap map) {
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", "http://blog.didispace.com");
        User u = new User();
        u.setName("后台数据测试777ntest11");
        u.setPassword("282409611@qq.com");
        u.setId(1L);

        User u2 = new User();
        u2.setName("222");
        u2.setId(2L);

        User u3 = new User();
        u3.setName("333");
        u3.setId(3L);

        map.addAttribute("u", u);
        List<User> list = new ArrayList<>();
        list.add(u);
        list.add(u2);
        list.add(u3);
        map.addAttribute("users", list);
        map.addAttribute("date", new Date());
        map.addAttribute("email", "123456789@qq.com");
        // return模板文件的名称，对应src/main/resources/templates/index.html
        return "thymeleaf";
    }

    @RequestMapping(value = "hello",
            produces = {"application/json", "application/xml"},
            method = RequestMethod.GET)
    @ResponseBody
    public User hello(ServletRequest request) {
        //return "hello";

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Map m = httpRequest.getParameterMap();
        Principal principal = httpRequest.getUserPrincipal();
        Enumeration<String> names = httpRequest.getAttributeNames();
        while (names.hasMoreElements()) {//以此取出头信息
            String key = (String) names.nextElement();
            Object value = httpRequest.getAttribute(key);//取出头信息内容
            System.out.println(key + "  :  " + value);
        }
        Enumeration<String> headers = httpRequest.getHeaderNames();
        while (headers.hasMoreElements()) {//以此取出头信息
            String headerName = (String) headers.nextElement();
            String headerValue = httpRequest.getHeader(headerName);//取出头信息内容
            System.out.println(headerName + "  :  " + headerValue);
        }
        User user = new User("shiro", "123456");
        user.setDate(new Date());
        return user;
    }

    @RequestMapping("/login")
    public String login(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Principal principal = httpRequest.getUserPrincipal();
        return "login";
    }

    /**
     * 登陆接口
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ModelAndView submit(String username, String password) {
        User user = new User("shiro", "123456");
        user.setRole(new Role("user"));
        try {
            // 如果登陆成功
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user
                        .getPassword());
                Subject subject = SecurityUtils.getSubject();
//                token.setRememberMe(true);//RememberMeAuthenticationToken接口，我们可以通过令牌设置“记住我”的功能
//                收集了实体/凭据信息之后，我们可以通过SecurityUtils工具类，获取当前的用户，然后通过调用login方法提交认证
//                如果login方法执行完毕且没有抛出任何异常信息，那么便认为用户认证通过。
//                之后在应用程序任意地方调用SecurityUtils.getSubject()
//                都可以获取到当前认证通过的用户实例，使用subject.isAuthenticated()
//                判断用户是否已验证都将返回true.
                subject.login(token);
//                testAnnotation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/member/index.html");
    }

    /**
     * 角色的注解貌似不起作用 暂时不知道为啥
     *
     * 此时可以使用程序 来判断 角色问题 Subject subject = SecurityUtils.getSubject();
     * if(subject.hasRole("user")){ System.out.println("***************"); }
     */
    @RequestMapping(value = "/anno", method = RequestMethod.GET)
    @RequiresRoles("member111")
    @RequiresPermissions("ww")
    private void testAnnotation() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("user")) {
            System.out.println("***************");
        }
        System.out.println("#########" + subject.isAuthenticated() + "##########");
    }

    //获取上传的文件夹，具体路径参考application.properties中的配置
    @Value("${web.upload-path}")
    private String uploadPath;

}
