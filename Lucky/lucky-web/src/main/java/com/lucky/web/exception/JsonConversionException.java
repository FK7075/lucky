package com.lucky.web.exception;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/5/31 3:29 上午
 */
public class JsonConversionException extends RuntimeException{

    public JsonConversionException(String serverIp, Class<?> targetClass, String jsonString){
        super("JSON转换异常！\n  Api                 : "+serverIp+"\n  Return Abbreviation : "+jsonString+"\n  Aims Type           : "+targetClass.getName()+"]");
    }

    public JsonConversionException(String jsonStr, Class<?> targetClass, Throwable e){
        super("JSON转换异常！ String: "+jsonStr+" ,Class: "+targetClass.getName(),e);
    }

}
