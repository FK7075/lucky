package com.lucky.aop.core;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.MethodUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 16:01
 */
public class JDKAopChain implements AopChain{

    private final List<AopPoint> points;

    private int index=-1;

    private Object target;

    private Object[] params;

    private final Method currMethod;


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

    @Override
    public Object[] getArgument() {
        return params;
    }

    @Override
    public void setArgument(Object[] arguments) {
        Assert.notNull(arguments, "Argument array passed to proceed cannot be null");
        Parameter[] parameters = currMethod.getParameters();
        if (arguments.length != parameters.length) {
            throw new IllegalArgumentException("Expecting " +
                    parameters.length + " arguments to proceed, " +
                    "but was passed " + arguments.length + " arguments");
        }

        for (int i = 0,j=parameters.length; i < j; i++) {
            if(arguments[i]==null){
                continue;
            }
            Class<?> argumentsClass = arguments[i].getClass();
            Class<?> paramsClass = parameters[i].getType();
            if((!paramsClass.equals(argumentsClass))||(!paramsClass.isAssignableFrom(argumentsClass))){
                throw new IllegalArgumentException("It is expected that the type of the [`"+i+"`] parameter is `"+paramsClass.getName()+"`, but the provided parameter type is `"+argumentsClass.getName()+"`");
            }
        }
        params=arguments;
    }

    public Object clone(){

        try {
            return super.clone();
        }catch (CloneNotSupportedException  e){
            throw new RuntimeException(e);
        }
    }
}
