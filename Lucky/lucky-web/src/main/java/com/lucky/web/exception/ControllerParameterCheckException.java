package com.lucky.web.exception;

/**
 * 参数校验异常
 * @author fk
 * @version 1.0
 * @date 2020/11/25 0025 11:43
 */
public class ControllerParameterCheckException extends RuntimeException{

    public ControllerParameterCheckException(String msg){
        super(msg);
    }
}
