package com.lucky.boot.conf;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/16 上午1:39
 */
public class WrongWebComponentTypeException extends RuntimeException{

    public WrongWebComponentTypeException(Class<?> sc,Class<?> ec,String componentType){
        super(String.format("注册[%s]组件失败，错误的类型[%s],与预期的类型[%s]不兼容！",componentType,ec,sc));
    }
}
