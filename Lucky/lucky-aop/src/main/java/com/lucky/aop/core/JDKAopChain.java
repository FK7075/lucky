package com.lucky.aop.core;

import com.lucky.utils.reflect.MethodUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 16:01
 */
public class JDKAopChain implements AopChain{

    private List<AopPoint> points;

    private int index=-1;

    private Object target;

    private Object[] params;

    private Method currMethod;


    public JDKAopChain(List<AopPoint> points, Object target, Object[] params,Method currMethod) {
        this.points = points;
        this.target = target;
        this.params = params;
        this.currMethod=currMethod;
    }

    int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object proceed() throws Throwable {
        Object result;
        if(++index==points.size()) {
//			result=MethodUtils.invoke(target,currMethod,params);
            result= MethodUtils.invoke(target,currMethod,params);
        }else {
            AopPoint point=points.get(index);
            result=point.proceed(this);
        }
        return result;
    }
}
