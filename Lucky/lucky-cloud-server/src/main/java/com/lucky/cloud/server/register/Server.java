package com.lucky.cloud.server.register;

import java.net.URL;

/**
 * 服务
 * @author fk
 * @version 1.0
 * @date 2021/1/27 0027 16:23
 */
public interface Server {

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



}
