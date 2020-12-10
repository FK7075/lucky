package com.lucky.aop.core;

import com.lucky.framework.container.Module;

import java.lang.reflect.Method;

/**
 * 表达式检验
 * @author fk
 * @version 1.0
 * @date 2020/12/10 0010 14:34
 */
public abstract class AopExecutionChecker {

    /** 切面方法*/
    protected Method aspectMethod;
    /** 切面表达式*/
    protected String positionExpression;

    public Method getAspectMethod() {
        return aspectMethod;
    }

    public void setAspectMethod(Method aspectMethod) {
        this.aspectMethod = aspectMethod;
    }

    public String getPositionExpression() {
        return positionExpression;
    }

    public void setPositionExpression(String positionExpression) {
        this.positionExpression = positionExpression;
    }

    public AopExecutionChecker() {
    }

    /**
     * 方法检验，检验方法是否要执行代理
     * @param method 带检验的方法
     * @return
     */
    protected abstract boolean methodExamine(Method method);

    /**
     * 类检验，检验类是否要执行代理
     * @param bean 带检验的模型
     * @return
     */
    protected abstract boolean classExamine(Module bean);

}
