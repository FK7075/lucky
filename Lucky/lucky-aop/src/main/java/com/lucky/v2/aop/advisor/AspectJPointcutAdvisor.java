package com.lucky.v2.aop.advisor;

import com.lucky.v2.aop.pointcut.AspectJExpressionPointcut;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:55
 */
public class AspectJPointcutAdvisor extends AbstractPointcutAdvisor{

    public AspectJPointcutAdvisor(String adviceBeanName, String expression) {
        super(adviceBeanName, expression);
        pointcut=new AspectJExpressionPointcut(expression);
    }
}
