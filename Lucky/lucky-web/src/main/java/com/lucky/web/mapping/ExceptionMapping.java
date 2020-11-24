package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.RepeatDefinitionExceptionHandlerException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个具体的异常映射器
 * @author fk7075
 * @version 1.0
 * @date 2020/11/24 9:12
 */
public class ExceptionMapping {

    /** ControllerAdvice对象*/
    private Object controllerAdvice;
    /** Controller方法*/
    private Method method;
    /** 作用范围*/
    private String[] scopes;
    /** 响应方式*/
    private Rest rest;
    /** 所要处理的异常*/
    private Class<? extends Throwable>[] exceptions;
    /** 运行ControllerAdvice方法需要的参数*/
    private Parameter[] parameters;

    public ExceptionMapping(Object controllerAdvice, Method method, String[] scopes, Rest rest, Class<? extends Throwable>[] exceptions) {
        this.controllerAdvice = controllerAdvice;
        this.method = method;
        this.scopes = scopes;
        this.rest = rest;
        this.parameters=method.getParameters();
        this.exceptions = exceptions;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public Object getControllerAdvice() {
        return controllerAdvice;
    }

    public void setControllerAdvice(Object controllerAdvice) {
        this.controllerAdvice = controllerAdvice;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<? extends Throwable>[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(Class<? extends Throwable>[] exceptions) {
        this.exceptions = exceptions;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    /**
     * 检验两个ExceptionMapping是否相互排斥
     * @param em 待检验的ExceptionMapping对象
     * @return
     */
    public boolean isRepel(ExceptionMapping em){
        List<String> scopeIntersect = scopeIntersect(em.getScopes());
        List<Class<? extends Throwable>> exceptionIntersect = exceptionIntersect(em.getExceptions());
        //异常与作用域其中一个不存在交集，则可判定两个异常处理器“不排斥”
        if(Assert.isEmptyCollection(scopeIntersect)||Assert.isEmptyCollection(exceptionIntersect)){
            return false;
        }
        throw new RepeatDefinitionExceptionHandlerException(scopeIntersect,exceptionIntersect,this.method,em.getMethod());
    }

    /**
     * 作用域取交集
     * @param scopes
     * @return
     */
    private List<String> scopeIntersect(String[] scopes){
        List<String> intersect=new ArrayList<>();
        for (String currScope : scopes) {
            for (String scope : this.scopes) {
                if(scope.equals(currScope)){
                    intersect.add(scope);
                }
            }
        }
        return intersect;
    }

    /**
     * 所要处理的异常取交集
     * @param exceptions
     * @return
     */
    private List<Class<? extends Throwable>> exceptionIntersect(Class<? extends Throwable>[] exceptions){
        List<Class<?extends Throwable>> intersect=new ArrayList<>();
        for (Class<? extends Throwable> currException : exceptions) {
            for (Class<? extends Throwable> exception : this.exceptions) {
                if(currException==exception){
                    intersect.add(exception);
                }
            }
        }
        return intersect;
    }
}
