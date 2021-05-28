package com.lucky.cloud.client.core;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午7:14
 */
public class ServiceRegistrationException extends RuntimeException{

    public ServiceRegistrationException(String zoneKey,String zone,Throwable e){
        super(String.format("Service registration exception！Failed to access [%s]`%s`",zoneKey,zone),e);
    }

    public ServiceRegistrationException(String zoneKey,String zone){
        super(String.format("Service registration exception！Your registration certificate is incorrect, or you do not have the right to register! [%s]`%s`",zoneKey,zone));
    }
}
