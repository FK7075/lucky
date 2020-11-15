package com.lucky.jacklamb.framework.scan;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/6/10 10:25 下午
 */
public class JarScanException extends  RuntimeException{

    public JarScanException(String message,Throwable e){
        super(message,e);
    }
}
