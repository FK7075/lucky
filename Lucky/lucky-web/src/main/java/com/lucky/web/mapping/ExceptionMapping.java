package com.lucky.web.mapping;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.web.core.Model;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.RepeatDefinitionExceptionHandlerException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
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
public class ExceptionMapping extends Mapping{


    /** 作用范围*/
    private String[] scopes;
    /** 所要处理的异常*/
    private Class<? extends Throwable>[] exceptions;

    public ExceptionMapping(Object controllerAdvice, Method method, String[] scopes, Rest rest, Class<? extends Throwable>[] exceptions) {
        this.object = controllerAdvice;
        this.mapping = method;
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

    public Class<? extends Throwable>[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(Class<? extends Throwable>[] exceptions) {
        this.exceptions = exceptions;
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
        if(Assert.isEmptyCollection(scopeIntersect)|| Assert.isEmptyCollection(exceptionIntersect)){
            return false;
        }
        throw new RepeatDefinitionExceptionHandlerException(scopeIntersect,exceptionIntersect,this.mapping,em.getMapping());
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

    /**
     * 执行ControllerAdvice方法
     * @param model 当前请求的Model对象
     * @return 方法执行后的返回值
     */
    public Object invoke(Model model, UrlMapping urlMapping, Throwable ex){
        Object[] params = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            if (Throwable.class.isAssignableFrom(type)) {
                params[i] = ex;
            } else if (Model.class.isAssignableFrom(type)) {
                params[i] = model;
            } else if (Method.class.isAssignableFrom(type)) {
                params[i] = urlMapping.getMapping();
            } else if (Class.class.isAssignableFrom(type)) {
                params[i] = urlMapping.getObject().getClass();
            } else if (HttpRequest.class.isAssignableFrom(type)) {
                params[i] = model.getRequest();
            } else if (HttpResponse.class.isAssignableFrom(type)) {
                params[i] = model.getResponse();
            } else if (HttpSession.class.isAssignableFrom(type)) {
                params[i] = model.getSession();
            } else if (ServletContext.class.isAssignableFrom(type)) {
                params[i] = model.getServletContext();
            } else if (Object[].class==type) {
                params[i] = urlMapping.getRunParams();
            } else if (Object.class.isAssignableFrom(type)) {
                params[i] = urlMapping.getObject();
            }
            i++;
        }
        final Object result = MethodUtils.invoke(object, mapping, params);
        return result;
    }

    public String getStrScopes(){
        if(scopes.length==0){
            return "_global_";
        }
        StringBuilder scopeStr=new StringBuilder();
        for (String scope : scopes) {
            scopeStr.append(scope).append(",");
        }
        return scopeStr.substring(0,scopeStr.length()-1);
    }
}
