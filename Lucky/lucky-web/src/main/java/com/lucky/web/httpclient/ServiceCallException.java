package com.lucky.web.httpclient;

/**
 * @author fk
 * @version 1.0
 * @date 2021/2/1 0001 10:04
 */
public class ServiceCallException extends RuntimeException {

    public ServiceCallException(int callType){
        super(String.format("错误的请求类型：%s",callType));
    }

}
