package com.lucky.cloud.server.core;

import java.util.Date;
import java.util.Map;

/**
 * 服务
 * @author fk
 * @version 1.0
 * @date 2021/1/27 0027 16:23
 */
public interface Server {

    String DEFAULT_LOGIN_PASSWORD = "LUCKY_PA$$W0RD";

    /**
     * 注册时间
     */
    Date registerTime();

    /**
     * 是否可以访问
     */
    default boolean isAccess(){
        return true;
    }

    /**
     * 获取服务名
     */
    String getServerName();

    /**
     * 服务的IP地址
     */
    String getIp();

    /**
     * 服务监听的端口
     */
    int getPort();

    /**
     * 通信协议
     */
    String getAgreement();

    /**
     * 获取服务的域
     */
    String getDomain();

    /**
     * 是否正常工作
     */
    boolean isNormalWork();

    /**
     * 资源调用
     * @param resource 资源定位符
     * @param param 参数列表
     * @param note 备注信息
     * @return 响应结果
     */
    Object call(String resource, Map<String,Object> param,Object note) throws Exception;


    /**
     * 返回服务注册时携带的密码
     * @return 密码
     */
    default String getLoginPassword(){
        return DEFAULT_LOGIN_PASSWORD;
    }

    /**
     * 判断两个服务是否等价
     * @param s 待比较的服务
     * @return
     */
    default boolean isEqual(Server s){
        return  getServerName().equals(s.getServerName())&&
                getIp().equals(s.getIp())&&
                getAgreement().equals(s.getAgreement())&&
                getPort()==s.getPort();
    }



}
