package com.lucky.web.mapping;

import java.lang.reflect.Method;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 10:25
 */
public class MappingMethodReturnTypeCheckException extends RuntimeException{

    public MappingMethodReturnTypeCheckException(Method method){
        super("Mapping方法返回值类型检查异常！`"+method+"` 的Rest类型为 `NO`(执行转发或重定向操作) ,该类型的Mapping方法如果有返回值，那么该返回值类型只能为 `java.lang.String` ,意外的返回值类型：`"+method.getReturnType()+"`!");
    }
}
