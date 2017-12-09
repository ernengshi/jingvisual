/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.security.shiro;

import cn.com.jingcloud.domain.entity.shiro.User;
import cn.com.jingcloud.domain.entity.shiro.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author liyong
 */
public class ShiroRealm extends AuthorizingRealm {
    
    private final static Logger LOG = LoggerFactory.getLogger(ShiroRealm.class);

//    @Autowired
//    @Qualifier("userService")
//    private UserService userService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LOG.info("roles and permission check");
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        //获取当前登录的用户名,等价于(String)principals.fromRealm(this.getName()).iterator().next()
        String name = (String) super.getAvailablePrincipal(principals);
        List<String> roles = new ArrayList<String>();
        Set<String> permisions = new HashSet<String>();
        permisions.add("user:query");//权限限定是  user:query
        // 简单默认一个用户与角色，实际项目应User user = userService.getByAccount(name);
        
        User user = new User("shiro", "123456");
        Role role = new Role("user");
        user.setRole(role);
        if (user.getName().equals(name)) {
            if (user.getRole() != null) {
                roles.add(user.getRole().getName());
            }
        } else {
            throw new AuthorizationException();
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 增加角色
        info.addRoles(roles);
        info.setStringPermissions(permisions);
        return info;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        LOG.info("check login " + token);
        // 简单默认一个用户,实际项目应User user = userService.getByAccount(token.getUsername());
        User user = new User("shiro", "123456");
        if (user == null) {
            throw new AuthorizationException();
        }
        SimpleAuthenticationInfo info = null;
        if (user.getName().equals(token.getUsername())) {
            info = new SimpleAuthenticationInfo(user.getName(), user.getPassword(), getName());
        }
        return info;
    }
    
    private void setSession(Object key, Object value) {
        Subject subject = SecurityUtils.getSubject();
        if (null != subject) {
            Session session = subject.getSession();
            if (null != session) {
                LOG.info("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
                session.setAttribute(key, value);
            }
        }
    }
}
