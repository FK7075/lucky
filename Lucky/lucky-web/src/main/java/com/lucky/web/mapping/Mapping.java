package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.utils.IpUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

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
    /** 定义响应的方式*/
    private Rest rest;
    /** 该请求支持的ip地址*/
    private Set<String> ips;
    /** 该请求支持的ip段范围*/
    private String[] ipSection;
    /** 运行Controller方法需要的参数*/
    private Parameter[] parameters;

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

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }

    public String[] getIpSection() {
        return ipSection;
    }

    public void setIpSection(String[] ipSection) {
        this.ipSection = ipSection;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public Mapping(String url, Object controller,
                   Method mapping, RequestMethod[] methods,
                   Rest rest,Set<String>ips,
                   String[] ipSection) {
        this.url = url;
        this.rest = rest;
        this.controller = controller;
        this.mapping = mapping;
        this.methods = methods;
        this.parameters=mapping.getParameters();
        this.ips=ips;
        this.ipSection=ipSection;
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
        if(!urlIsEquals(currMapping.getUrl())){
            return false;
        }
        //支持的请求类型校验
        RequestMethod[] currMethods = currMapping.getMethods();
        for (RequestMethod currMethod : currMethods) {
            if(methodIsEquals(currMethod)){
                return true;
            }
        }
        return false;
    }

    /**
     * 请求类型校验
     * @param method 待校验的请求类型
     * @return
     */
    public boolean methodIsEquals(RequestMethod method){
        for (RequestMethod requestMethod : getMethods()) {
            if(method==requestMethod){
                return true;
            }
        }
        return false;
    }

    /**
     * URL校验
     * @param url 待验证的URL
     * @return
     */
    public boolean urlIsEquals(String url){
        return getUrl().equals(url);
    }

    /**
     * 执行Controller方法
     * @param methodParams 执行方法需要的参数
     * @return 方法执行后的返回值
     */
    public Object invoke(Object[] methodParams){
        Object result = MethodUtils.invoke(controller, mapping, methodParams);
        return result;
    }

    public boolean ipExistsInRange(String ip) {
        if(ipSection==null||ipSection.length==0) {
            return true;
        }
        for(String hfip:ipSection) {
            if(hfip.startsWith("!")) {//判断该ip是否属于非法IP段
                if(IpUtil.ipExistsInRange(ip,hfip.substring(1))) {
                    return false;
                }
            }else if(IpUtil.ipExistsInRange(ip,hfip)){//判断该ip是否属性合法ip段
                return true;
            }
        }
        return false;//非非法ip段也非合法ip段，即为未注册ip，不给予通过
    }

    public boolean ipISCorrect(String currip) {
        if(ips.isEmpty()) {
            return true;
        }
        if("localhost".equals(currip)) {
            currip="127.0.0.1";
        }
        for(String ip:ips) {
            if(ip.startsWith("!")) {//判断该ip是否属于非法IP
                if(currip.equals(ip.substring(1))) {
                    return false;
                }
            }else if(currip.equals(ip)) {//判断该ip是否属性合法ip
                return true;
            }
        }
        return false;//非非法ip也非合法ip，即为未注册ip，不给予通过
    }
}
