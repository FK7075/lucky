package com.lucky.v2.aop.advisor;

import com.lucky.v2.aop.pointcut.Pointcut;

/**
 * 基于切入点的通知者实现
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:54
 */
public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();
}
