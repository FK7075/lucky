package com.lucky.web.mapping;

import com.lucky.utils.io.utils.AntPathMatcher;
import com.lucky.web.enums.Rest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午12:27
 */
public abstract class Mapping {

    protected static final AntPathMatcher antPathMatcher=new AntPathMatcher();

    /** 映射的唯一ID*/
    protected String id;
    /** Controller对象*/
    protected Object object;
    /** Controller方法*/
    protected Method mapping;
    /** 定义响应的方式*/
    protected Rest rest;
    /** 运行Controller方法需要参数的类型*/
    protected Parameter[] parameters;
    /** 运行Controller方法需要的参数*/
    protected Object[] runParams;
    /** 该映射是否已经被禁用*/
    protected boolean isDisable;


    public Mapping() {
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMapping() {
        return mapping;
    }

    public void setMapping(Method mapping) {
        this.mapping = mapping;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getRunParams() {
        return runParams;
    }

    public void setRunParams(Object[] runParams) {
        this.runParams = runParams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }
}
