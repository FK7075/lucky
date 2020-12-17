package com.lucky.jacklamb.exception;

public class CreateMapperException extends RuntimeException {

    public CreateMapperException(Class<?> mapperClass, Throwable e){
        super("无法创建 \""+mapperClass.getName()+"\" 的代理类！",e);
    }
}
