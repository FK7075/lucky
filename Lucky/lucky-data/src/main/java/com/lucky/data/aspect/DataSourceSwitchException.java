package com.lucky.data.aspect;/**
 * @author  fk
 * @date  2021/1/12 0012 9:48
 * @version 1.0
 */
public class DataSourceSwitchException extends RuntimeException{

    public DataSourceSwitchException(Throwable e,String msg){
        super(msg,e);
    }
}
