package com.lucky.cloud.client.conf;

import com.lucky.boot.conf.ServerConfig;
import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.utils.base.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午6:21
 */
public class LuckyCloudClientConfig extends LuckyConfig {

    private static final Logger log = LoggerFactory.getLogger("c.l.c.c.c.LuckyCloudClientConfig");
    private static LuckyCloudClientConfig clientConfig;
    private final Map<String,String> zones;
    private String name;
    private Integer port;
    private String agreement;

    public Map<String, String> getZones() {
        return zones;
    }


    public void addZone(String key,String zone){
        zone=zone.endsWith("/")?zone.substring(0,zone.length()-1):zone;
        zones.put(key,zone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private LuckyCloudClientConfig(){
        zones=new HashMap<>();
    }

    public static LuckyCloudClientConfig defaultLuckyCloudClientConfig(){
        if(clientConfig==null){
            clientConfig=new LuckyCloudClientConfig();
            clientConfig.setAgreement("HTTP");
            clientConfig.setPort(ServerConfig.getServerConfig().getPort());
            clientConfig.setFirst(true);
        }
        return clientConfig;
    }

    public static LuckyCloudClientConfig getLuckyCloudClientConfig(){
        LuckyCloudClientConfig clientConfig = defaultLuckyCloudClientConfig();
        if(clientConfig.isFirst()){
            YamlParsing.loadClient(clientConfig);
        }
        return clientConfig;
    }

    public void check(){
        if(Assert.isBlankString(name)){
            throw new IllegalArgumentException("initialization cloud client failed: `Client name is empty！`");
        }
        if(Assert.isEmptyMap(zones)){
            throw new IllegalArgumentException("initialization cloud client failed: `Server address is not configured！`");
        }
        if(Assert.isBlankString(agreement)){
            throw new IllegalArgumentException("initialization cloud client failed: `Client access agreement is empty！`");
        }
        if(Assert.isNull(port)){
            throw new IllegalArgumentException("initialization cloud client failed: `Client port is empty！`");
        }
    }

    @Override
    public void loadYaml() {
        YamlParsing.loadClient(clientConfig);
    }
}
