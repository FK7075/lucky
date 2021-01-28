package com.lucky.cloud.server.core;

import com.lucky.utils.base.Assert;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.httpclient.HttpClientCall;
import com.lucky.web.webfile.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 11:31
 */
public class HttpServer implements Server{

    private String name;
    private String ip;
    private Integer port;
    private Date ctime;
    private String agreement;

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
    public Object call(String resource, Map<String, Object> param, Object method) throws Exception {
        Assert.notNull(resource,"未指定请求的资源！resource is null");
        String resourceURL=getResourceURL(resource);
        RequestMethod requestMethod= (RequestMethod) method;
        if(isMultipartFileMap(param)){
            return HttpClientCall.uploadFile(resourceURL, param);
        }
        return HttpClientCall.call(resourceURL, requestMethod, param);
    }

    private String getResourceURL(String resource){
        resource=resource.startsWith("/")?resource:"/"+resource;
        return agreement.toLowerCase()+"://"+ip+":"+port+resource;
    }

    private boolean isMultipartFileMap(Map<String,Object> params){
        if(Assert.isEmptyMap(params)){
            return false;
        }
        for(Map.Entry<String,Object> entry:params.entrySet()){
            Object paramValue = entry.getValue();
            if(paramValue instanceof File[]){
                return true;
            }
            if(paramValue instanceof MultipartFile[]){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s://%s:%s",name,agreement.toLowerCase(),ip,port);
    }
}
