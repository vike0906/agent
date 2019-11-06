package com.vike.agent.common;

/**
 * @author: lsl
 * @createDate: 2019/11/6
 */
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(int code){
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
