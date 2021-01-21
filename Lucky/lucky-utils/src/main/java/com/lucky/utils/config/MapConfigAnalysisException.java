package com.lucky.utils.config;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 15:09
 */
public class MapConfigAnalysisException extends RuntimeException {

    public MapConfigAnalysisException(String msg,Throwable e){
        super(msg,e);
    }

    public MapConfigAnalysisException(String msg){
        super(msg);
    }
}
