package com.lucky.quartz.exception;

import java.lang.reflect.Method;

public class CronExpressionException extends RuntimeException {

    public CronExpressionException(Method method,String errExpression){
        super("非法的cron表达式：'"+errExpression+"'。 错误位置："+method);
    }
}
