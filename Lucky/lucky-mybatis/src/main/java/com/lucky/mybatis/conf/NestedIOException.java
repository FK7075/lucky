package com.lucky.mybatis.conf;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/10 上午1:57
 */
public class NestedIOException extends RuntimeException{
    public NestedIOException(String msg,Exception ex){
        super(msg,ex);
    }
}
