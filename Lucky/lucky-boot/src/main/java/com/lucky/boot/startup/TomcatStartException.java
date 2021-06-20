package com.lucky.boot.startup;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/12 上午1:06
 */
public class TomcatStartException extends RuntimeException{

    public TomcatStartException(Exception e){
        super("Tomcat failed to start. ",e);
    }
}
