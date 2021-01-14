package com.lucky.framework.container.factory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 9:40
 */
public class BeanFactoryInitializationException extends RuntimeException{

    public BeanFactoryInitializationException(String msg){
        super(msg);
    }

    public BeanFactoryInitializationException(Exception e,String msg){
        super(msg,e);
    }
}
