package com.vike.agent.utils;

import com.vike.agent.entity.SysUser;
import org.apache.shiro.SecurityUtils;

/**
 * @author: lsl
 * @createDate: 2019/3/14
 */
public class ShiroUtil {

    /**
     * 获取SysUser实例
     */
    public static SysUser getUser(){
        SysUser sysUser = SecurityUtils.getSubject().getPrincipals().oneByType(SysUser.class);
        return sysUser;
    }

    /**
     * 获取用户名
     */
    public static String getUserName(){
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        return userName;
    }

    /**
     * 获取用户ID
     */
    public static long getUserId(){
        return getUser().getId();
    }
}
