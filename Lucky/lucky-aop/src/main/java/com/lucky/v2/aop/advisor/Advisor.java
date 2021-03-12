package com.lucky.v2.aop.advisor;

/**
 * 切面，用于组织通知和切入点
 * 组合Advice和Pointcut
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:50
 */
public interface Advisor {

    //通知的唯一ID
    String getAdviceBeanName();

    //通知作用的切入点
    String getExpression();
}
