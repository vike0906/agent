package com.vike.agent.config;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.dao.SysUserRepository;
import com.vike.agent.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/3/13
 */
@Slf4j
public class CustomizeShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String name = (String)principalCollection.getPrimaryPrincipal();
        Optional<SysUser> op = sysUserRepository.findByLoginName(name);
        if(!op.isPresent())return null;
        SysUser user = op.get();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(String.valueOf(user.getRole().getId()));
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        String name = authenticationToken.getPrincipal().toString();
        /**剔除重复登陆用户
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager )SecurityUtils.getSecurityManager();
        DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
        Collection<Session> activeSessions = sessionManager.getSessionDAO().getActiveSessions();
        for (Session session:activeSessions) {
            if(name.equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))){
                sessionManager.getSessionDAO().delete(session);
            }
        }*/
        Optional<SysUser> op = sysUserRepository.findByLoginName(name);
        if (!op.isPresent()) {
            log.error("用户不存在");
            throw new CustomizeShiroException("用户名或密码错误");
        }
        SysUser user = op.get();
        if(GloableConstant.CANCEL_STATUS ==user.getStatus()){
            log.error("账户已注销");
            throw new CustomizeShiroException("用户已被禁用");
        }else {
            List<Object> principals = new ArrayList<Object>();
            principals.add(user.getName());
            principals.add(user);

            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(principals, user.getPassword(), getName());
            return simpleAuthenticationInfo;
        }
    }
}
