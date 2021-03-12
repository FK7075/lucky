package com.lucky.aop.aspectj;

import com.lucky.aop.core.AopExecutionChecker;
import com.lucky.framework.container.Module;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 11:47
 */
public class AspectJAopExecutionChecker extends AopExecutionChecker {

    private AspectJExpressionPointcut aspectJExpressionPointcut;

    @Override
    public void setPositionExpression(String positionExpression) {
        super.setPositionExpression(positionExpression);
        aspectJExpressionPointcut=new AspectJExpressionPointcut(positionExpression);
    }

    @Override
    protected boolean methodExamine(Class<?> targetClass, Method method) {
        return aspectJExpressionPointcut.matchsMethod(method,targetClass);
    }

    @Override
    protected boolean classExamine(Module bean) {
        return aspectJExpressionPointcut.matchsClass(bean.getOriginalType());
    }
}
