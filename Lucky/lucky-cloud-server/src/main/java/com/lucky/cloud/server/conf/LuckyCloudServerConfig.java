package com.lucky.cloud.server.conf;

import com.lucky.boot.conf.ServerConfig;
import com.lucky.cloud.server.core.Server;
import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.utils.dm5.MD5Utils;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午6:26
 */
public class LuckyCloudServerConfig extends LuckyConfig {

    private static LuckyCloudServerConfig serverConfig;
    /** 服务名*/
    private String name;
    /** 本机IP*/
    private String ip;
    /** 监听端口*/
    private Integer port;
    /** 注册密码*/
    private String password;
    /** 合法的ID*/
    private String[] legalIP;
    /** 合法的IP段*/
    private String[] legalIpSection;
    /** 使用的协议*/
    private String agreement;
    /** 心跳检测的时间间隔*/
    private long detectionInterval;
    /** 是注册自己*/
    private boolean registerYourself;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    private void setPort(Integer port) {
        this.port = port;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public long getDetectionInterval() {
        return detectionInterval;
    }

    public void setDetectionInterval(long detectionInterval) {
        this.detectionInterval = detectionInterval;
    }

    public boolean isRegisterYourself() {
        return registerYourself;
    }

    public void setRegisterYourself(boolean registerYourself) {
        this.registerYourself = registerYourself;
    }

    public static LuckyCloudServerConfig getServerConfig() {
        return serverConfig;
    }

    public static void setServerConfig(LuckyCloudServerConfig serverConfig) {
        LuckyCloudServerConfig.serverConfig = serverConfig;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = MD5Utils.md5UpperCase(password,"LUCKY_SALT_XFL_FK",21);
    }

    public String[] getLegalIP() {
        return legalIP;
    }

    public void setLegalIP(String[] legalIP) {
        this.legalIP = legalIP;
    }

    public String[] getLegalIpSection() {
        return legalIpSection;
    }

    public void setLegalIpSection(String[] legalIpSection) {
        this.legalIpSection = legalIpSection;
    }

    private LuckyCloudServerConfig(){}

    public static LuckyCloudServerConfig defaultLuckyCloudServerConfig(){
        if(serverConfig==null){
            serverConfig=new LuckyCloudServerConfig();
            serverConfig.setRegisterYourself(false);
            serverConfig.setAgreement("HTTP");
            serverConfig.setIp("127.0.0.1");
            serverConfig.setPassword(Server.DEFAULT_LOGIN_PASSWORD);
            serverConfig.setDetectionInterval(5*1000L);
            serverConfig.setPort(ServerConfig.getServerConfig().getPort());
            serverConfig.setFirst(true);
        }
        return serverConfig;
    }

    public static LuckyCloudServerConfig getLuckyCloudServerConfig(){
        serverConfig = defaultLuckyCloudServerConfig();
        if(serverConfig.isFirst()){
            YamlParsing.loadServer(serverConfig);
        }
        return serverConfig;
    }


    @Override
    public void loadYaml() {
        YamlParsing.loadServer(serverConfig);
    }
}
