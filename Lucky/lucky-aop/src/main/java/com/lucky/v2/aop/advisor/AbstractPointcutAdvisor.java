package com.lucky.v2.aop.advisor;

import com.lucky.v2.aop.pointcut.Pointcut;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:59
 */
public  class AbstractPointcutAdvisor implements PointcutAdvisor{

    protected String adviceBeanName;
    protected String expression;
    protected Pointcut pointcut;

    public AbstractPointcutAdvisor(String adviceBeanName, String expression) {
        super();
        this.adviceBeanName = adviceBeanName;
        this.expression = expression;
    }

    @Override
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
