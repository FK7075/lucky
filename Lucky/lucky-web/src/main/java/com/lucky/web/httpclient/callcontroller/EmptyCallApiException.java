package com.lucky.web.httpclient.callcontroller;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/3 0003 9:27
 */
public class EmptyCallApiException extends RuntimeException{

    public EmptyCallApiException(String key){
        super("空的Ca");
    }

}
