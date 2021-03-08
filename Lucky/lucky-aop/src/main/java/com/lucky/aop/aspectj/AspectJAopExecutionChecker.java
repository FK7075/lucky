package com.lucky.aop.aspectj;

import com.lucky.aop.core.AopExecutionChecker;
import com.lucky.framework.container.Module;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/7 上午9:22
 */
public class AspectJAopExecutionChecker extends AopExecutionChecker {

    private PointcutExpression pe;

    @Override
    public void setPositionExpression(String positionExpression) {
        super.setPositionExpression(positionExpression);
        PointcutParser parser=
                PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();
        pe=parser.parsePointcutExpression(positionExpression);
    }

    @Override
    protected boolean methodExamine(Class<?> targetClass, Method method) {
        ShadowMatch shadowMatch = pe.matchesMethodExecution(method);
        if(shadowMatch.alwaysMatches()){
            return true;
        }
        if(shadowMatch.neverMatches()){
            return false;
        }
        return shadowMatch.maybeMatches();
    }

    @Override
    protected boolean classExamine(Module bean) {
        return pe.couldMatchJoinPointsInType(bean.getOriginalType());
    }
}
