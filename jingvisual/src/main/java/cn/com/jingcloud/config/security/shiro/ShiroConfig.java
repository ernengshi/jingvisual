/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.security.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author ly
 */
@Configuration
public class ShiroConfig {

    private final static Logger LOG = LoggerFactory.getLogger(ShiroConfig.class);

    /**
     * * ShiroFilterFactoryBean 处理拦截资源文件问题。 *
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在 *
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager * * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过 *
     * 3、部分过滤器可指定参数，如perms，roles *
     */
    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean(); // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面 
        shiroFilterFactoryBean.setLoginUrl("/login.html");
// 登录成功后要跳转的链接 
//        shiroFilterFactoryBean.setSuccessUrl("/member/index.html");
// 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
// 拦截器. 
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
// 配置不会被拦截的链接 顺序判断 
        filterChainDefinitionMap.put("/static/**", "anon");
        //resources
        filterChainDefinitionMap.put("/resources/**", "anon");
        filterChainDefinitionMap.put("/ajaxLogin", "anon");
// 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了 
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/add", "perms[权限添加]");
// <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了; 
// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问--> 
        filterChainDefinitionMap.put("/member/**", "authc,roles[\"user\"],perms[user:query]");
        filterChainDefinitionMap.put("/api/**", "authcBasic");//对外开放的api使用这种方式验证
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        LOG.info("shiro filter factory bean autowired");
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(shiroRealm());
        return securityManager;
    }

    /**
     * 身份认证realm; (这个需要自己写，账号密码校验；权限等)
     *
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        return shiroRealm;
    }

    /**
     * springboot thymeleaf和shiro标签整合
     *
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
