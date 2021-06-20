package com.lucky.web.httpclient;


import com.lucky.utils.base.Assert;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求头管理器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/10 下午10:13
 */
public class RequestHeaderManage {

    private final static String ADD_HEADER_TEMP = "[ADD] %s <===> %s";
    private final static String SET_HEADER_TEMP = "[SET] %s <===> %s";

    private final List<String> headerList = new ArrayList<>();

    /**
     * 添加一个请求头
     * @param name   名称
     * @param header 头信息
     */
    public void addHeader(String name,String header){
        check(name, header);
        headerList.add(String.format(ADD_HEADER_TEMP,name,header));
    }

    /**
     * 设置一个请求头
     * @param name   名称
     * @param header 头信息
     */
    public void setHeader(String name,String header){
        check(name, header);
        headerList.add(String.format(SET_HEADER_TEMP,name,header));
    }

    void initHeader(HttpRequestBase request){
        String add = "[ADD] ";
        String set = "[SET] ";
        for (String headerInfo : headerList) {
            if(headerInfo.startsWith(add)){
                String[] headerArray = headerInfo.substring(add.length()).split(" <===> ");
                request.addHeader(headerArray[0],headerArray[1]);
            }else if(headerInfo.startsWith(set)){
                String[] headerArray = headerInfo.substring(set.length()).split(" <===> ");
                request.setHeader(headerArray[0],headerArray[1]);
            }
        }

    }


    private void check(String name,String header){
        Assert.notNull(name,"Header name is null");
        Assert.notNull(header,"Header value is null");
    }

}
