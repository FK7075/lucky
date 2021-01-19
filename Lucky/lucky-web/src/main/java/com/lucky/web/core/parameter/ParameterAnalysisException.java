package com.lucky.web.core.parameter;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/1 0001 14:55
 */
public class ParameterAnalysisException extends RuntimeException {

    public ParameterAnalysisException(String msg,Throwable e){
        super(msg,e);
    }

    public ParameterAnalysisException(String msg){
        super(msg);
    }
}
