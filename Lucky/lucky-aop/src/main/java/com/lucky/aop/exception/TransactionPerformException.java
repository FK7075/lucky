package com.lucky.aop.exception;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/2 12:15 下午
 */
public class TransactionPerformException extends RuntimeException{

    public TransactionPerformException(Throwable e, String message){
        super(message,e);
    }
}
