package com.lucky.jacklamb.mapper.exception;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/6 1:07 上午
 */
public class XMLParsingException extends RuntimeException{

    public XMLParsingException(String message,Throwable e){
        super(message,e);
    }

    public XMLParsingException(Throwable e){
        super("XML解析异常！",e);
    }
}
