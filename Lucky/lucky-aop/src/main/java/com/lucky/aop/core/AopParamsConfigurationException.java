package com.lucky.aop.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/25 15:33
 */
public class AopParamsConfigurationException extends RuntimeException {

    private static final Logger log= LogManager.getLogger(AopParamsConfigurationException.class);

    public AopParamsConfigurationException(String msg){
        super(msg);
        log.error(msg,this);
    }

    public AopParamsConfigurationException(String msg,Throwable e){
        super(msg,e);
        log.error(msg,this);
    }
}
