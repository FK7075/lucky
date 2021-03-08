package com.lucky.aop.core;

import com.lucky.framework.container.Module;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/7 上午9:22
 */
public class AspectJAopExecutionChecker extends AopExecutionChecker{

    private AspectJExpressionPointcut ape=new AspectJExpressionPointcut();

    @Override
    public void setPositionExpression(String positionExpression) {
        super.setPositionExpression(positionExpression);
        ape.setExpression(positionExpression);
    }

    @Override
    protected boolean methodExamine(Class<?> targetClass, Method method) {
        return ape.matches(method, targetClass);
    }

    @Override
    protected boolean classExamine(Module bean) {
        return ape.matches(bean.getOriginalType());
    }
}
