package com.lucky.web.exception;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 重复定义的异常处理器异常
 * @author fk7075
 * @version 1.0
 * @date 2020/11/24 9:59
 */
public class RepeatDefinitionExceptionHandlerException extends RuntimeException{

    public RepeatDefinitionExceptionHandlerException(List<String> scopes, List<Class<? extends Throwable>> exception, Method method1,Method method2){
        super(String.format("重复定义的异常处理器异常！对于同一个作用域 \"%s\" 的同一批异常 \"%s\" ,存在多个处理器：1:\"[%s]\" 2:\"[%s]\"",
                scopes,exception,method1,method2));
    }
}
