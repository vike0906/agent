package com.vike.agent.config;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author: lsl
 * @createDate: 2019/10/31
 */
public class CustomizeShiroException extends AuthenticationException {

    public CustomizeShiroException() {
    }

    public CustomizeShiroException(String message) {
        super(message);
    }

}
