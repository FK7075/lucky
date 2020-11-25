package com.lucky.web.exception;

import java.lang.reflect.Method;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午2:44
 */
public class InitRunException extends RuntimeException{

    public InitRunException(Method method){
        super("被@InitRun注解标注的Controller初始化方法必须是「无参方法」！错误位置: "+method);
    }
}
