package com.lucky.cloud.server.conf;

import com.lucky.boot.conf.ServerConfig;
import com.lucky.framework.confanalysis.LuckyConfig;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午6:26
 */
public class LuckyCloudServerConfig extends LuckyConfig {

    private static LuckyCloudServerConfig serverConfig;
    private String name;
    private String ip;
    private Integer port;
    private String agreement;
    private long detectionInterval;
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

    private LuckyCloudServerConfig(){}

    public static LuckyCloudServerConfig defaultLuckyCloudServerConfig(){
        if(serverConfig==null){
            serverConfig=new LuckyCloudServerConfig();
            serverConfig.setRegisterYourself(false);
            serverConfig.setAgreement("HTTP");
            serverConfig.setIp("127.0.0.1");
            serverConfig.setDetectionInterval(5*1000L);
            serverConfig.setPort(ServerConfig.getServerConfig().getPort());
            serverConfig.setFirst(true);
        }
        return serverConfig;
    }

    public static LuckyCloudServerConfig getLuckyCloudServerConfig(){
        LuckyCloudServerConfig clientConfig = defaultLuckyCloudServerConfig();
        if(clientConfig.isFirst()){
            YamlParsing.loadServer(clientConfig);
        }
        return clientConfig;
    }


}
