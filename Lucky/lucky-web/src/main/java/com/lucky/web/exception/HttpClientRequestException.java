package com.lucky.web.exception;

public class HttpClientRequestException extends RuntimeException {

    public HttpClientRequestException(String massage){
        super(massage);
    }
}
