package com.lucky.aop.core;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/10/12 9:21
 */
public class AopExecuteException extends RuntimeException {

    public AopExecuteException(Throwable e){
        super("AOP代理执行失败，在执行真实方法时出现异常，嵌套异常为："+e.getMessage(),e);
    }
}
