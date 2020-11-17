package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.web.enums.RequestMethod;

import java.lang.reflect.Method;

/**
 * 一个具体的URL映射
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:53
 */
public class Mapping {

    /** URL*/
    private String url;
    /** Controller对象*/
    private Object controller;
    /** Controller方法*/
    private Method mapping;
    /** 当前的请求类型*/
    private RequestMethod[] methods;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMapping() {
        return mapping;
    }

    public void setMapping(Method mapping) {
        this.mapping = mapping;
    }

    public RequestMethod[] getMethods() {
        return methods;
    }

    public void setMethods(RequestMethod[] methods) {
        this.methods = methods;
    }

    public Mapping(String url, Object controller, Method mapping, RequestMethod[] methods) {
        this.url = url;
        this.controller = controller;
        this.mapping = mapping;
        this.methods = methods;
    }

    /**
     * 判断当前Mapping是否等价于本身
     * @param currMapping 当前Mapping
     * @return
     */
    public boolean isEquals(Mapping currMapping){
        if(Assert.isNull(currMapping)){
            return false;
        }
        //URL校验
        if(!getUrl().equals(currMapping.getUrl())){
            return false;
        }
        //支持的请求类型校验
        RequestMethod[] currMethods = currMapping.getMethods();
        for (RequestMethod method : this.methods) {
            for (RequestMethod currMethod : currMethods) {
                if(method==currMethod){
                    return true;
                }
            }
        }
        return false;
    }
}
