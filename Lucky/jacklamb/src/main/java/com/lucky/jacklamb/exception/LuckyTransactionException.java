package com.lucky.jacklamb.exception;

public class LuckyTransactionException extends RuntimeException {

    public LuckyTransactionException(String message, Throwable e){
        super(message,e);
    }

    public LuckyTransactionException(String message){
        super(message);
    }
}
