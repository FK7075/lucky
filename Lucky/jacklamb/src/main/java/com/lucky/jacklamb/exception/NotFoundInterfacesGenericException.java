package com.lucky.jacklamb.exception;

public class NotFoundInterfacesGenericException extends RuntimeException{

    public NotFoundInterfacesGenericException(Class<?> clzz, Throwable e){
        super("子接口在继承LuckyMapper<T>接口时必须设置泛型的类型！,在 "+clzz.getName()+" 子接口中没有找到必须的泛型！");
    }
}
