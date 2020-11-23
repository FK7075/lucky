package com.lucky.web.exception;

/**
 * @Author jackfu
 * @Date 2020/4/6 10:19 下午
 * @Version 1.0
 **/


public class RequestFileSizeCrossingException extends Exception {

    public RequestFileSizeCrossingException(String message) {
        super(message);
    }
}
