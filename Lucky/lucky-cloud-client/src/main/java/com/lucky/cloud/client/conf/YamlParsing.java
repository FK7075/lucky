package com.lucky.cloud.client.conf;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午6:24
 */
public abstract class YamlParsing {

    private static YamlConfAnalysis yaml;

    public static void loadClient(LuckyCloudClientConfig conf){
        yaml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String, Object> map, LuckyCloudClientConfig client) {
        Object clientObj = map.get("cloud-client");
        if(clientObj instanceof Map){
            Map<String,Object> clientMap= (Map<String, Object>) clientObj;
            Object appName = clientMap.get("application-name");
            if(appName instanceof String){
                client.setName((String) get(appName.toString()));
            }
            Object agreement = clientMap.get("agreement");
            if(agreement instanceof String){
                client.setAgreement((String) get(agreement.toString()));
            }
            Object serverUrl = clientMap.get("service-url");
            if(serverUrl instanceof String){
                client.addZone("defaultZone", (String) get(serverUrl.toString()));
            }
            if(serverUrl instanceof Map){
                Map<String,String> serverUrls= (Map<String, String>) serverUrl;
                for (Map.Entry<String,String> entry:serverUrls.entrySet()){
                    client.addZone(entry.getKey(), (String) get(entry.getValue()));
                }
            }
        }
    }
}
