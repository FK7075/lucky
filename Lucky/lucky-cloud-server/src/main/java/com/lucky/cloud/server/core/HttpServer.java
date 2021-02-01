package com.lucky.cloud.server.core;

import com.lucky.utils.base.Assert;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.httpclient.HttpClientCall;
import com.lucky.web.httpclient.HttpProxyUtils;
import com.lucky.web.httpclient.callcontroller.CallControllerMethodInterceptor;
import com.lucky.web.webfile.MultipartFile;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 11:31
 */
public class HttpServer implements Server {

    private final static String IS_NORMAL_WORK_RESOURCE="/lucky/workState";
    private final String name;
    private final String ip;
    private final Integer port;
    private final Date ctime;
    private final String agreement;

    public HttpServer(String name, String ip, Integer port){
        this(name,ip,port,"HTTP");
    }

    public HttpServer(String name, String ip, Integer port, String agreement) {
        if(Assert.isBlankString(name)){
            throw new IllegalArgumentException("服务名不合法："+name);
        }
        if(Assert.isBlankString(ip)){
            throw new IllegalArgumentException("IP不合法："+name);
        }
        if(Assert.isBlankString(agreement)){
            agreement="HTTP";
        }
        Assert.notNull(port,"port不合法："+port);
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.ctime = new Date();
        this.agreement = agreement;
    }

    @Override
    public Date registerTime() {
        return this.ctime;
    }

    @Override
    public String getServerName() {
        return this.name;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getAgreement() {
        return this.agreement;
    }

    @Override
    public String getDomain() {
        return agreement.toLowerCase()+"://"+ip+":"+port;
    }

    @Override
    public boolean isNormalWork() {
        try {
            Object result = call(IS_NORMAL_WORK_RESOURCE, new HashMap<>(), RequestMethod.GET);
            if(result instanceof String){
                String resultStr= (String) result;
                return "UP".equals(resultStr);
            }
            return false;
        }catch (Exception e){
            return e instanceof SocketTimeoutException;
        }
    }

    @Override
    public Object call(String resource, Map<String, Object> param, Object method) throws Exception {
        Assert.notNull(resource,"未指定请求的资源！resource is null");
        String resourceURL=getResourceURL(resource);
        RequestMethod requestMethod= (RequestMethod) method;
        if(HttpProxyUtils.isMultipartFileMap(param)){
            return HttpClientCall.uploadFile(resourceURL, param);
        }
        return HttpClientCall.call(resourceURL, requestMethod, param);
    }

    private String getResourceURL(String resource){
        resource=resource.startsWith("/")?resource:"/"+resource;
        return getDomain()+resource;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s://%s:%s",name,agreement.toLowerCase(),ip,port);
    }
}
