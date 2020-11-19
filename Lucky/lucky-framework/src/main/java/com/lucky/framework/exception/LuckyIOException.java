package com.lucky.framework.exception;

import java.io.IOException;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 14:34
 */
public class LuckyIOException extends RuntimeException{

    public LuckyIOException(IOException ioe){
        super(ioe);
    }
}
