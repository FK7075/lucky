package com.lucky.cloud.server.conf;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;

import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午6:24
 */
public abstract class YamlParsing {

    private static YamlConfAnalysis yaml;

    public static void loadServer(LuckyCloudServerConfig conf){
        yaml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String, Object> map, LuckyCloudServerConfig server) {
        Object serverObj = map.get("cloud-server");
        if(serverObj instanceof Map){
            Map<String,Object> serverMap= (Map<String, Object>) serverObj;
            Object appName = serverMap.get("application-name");
            if(appName instanceof String){
                server.setName((String) get(appName.toString()));
            }
            Object agreement = serverMap.get("agreement");
            if(agreement instanceof String){
                server.setAgreement((String) get(agreement.toString()));
            }
            Object address = serverMap.get("address");
            if(address instanceof String){
                server.setIp((String) get(address.toString()));
            }
            Object ry = serverMap.get("register-yourself");
            if(ry instanceof Boolean){
                server.setRegisterYourself((Boolean) ry);
            }
            if(ry instanceof String){
                server.setRegisterYourself((Boolean) JavaConversion.strToBasic(get(ry.toString()).toString(),boolean.class));
            }

            Object di = serverMap.get("detection-interval");
            if(di instanceof Long){
                server.setDetectionInterval((Long) di);
            }else if(di!=null){
                server.setDetectionInterval((Long) JavaConversion.strToBasic(get(di.toString()).toString(),long.class,true));
            }

            Object password = serverMap.get("password");
            if(password != null){
                server.setPassword(get(password.toString()).toString());
            }

            Object legalIps = serverMap.get("legal-ips");
            if(legalIps instanceof List){
                List<String> ipList = (List<String>) legalIps;
                String[] ipArray = new String[ipList.size()];
                int i = 0;
                for (String ip : ipList) {
                    ipArray[i++] = get(ip).toString().trim();
                }
                server.setLegalIP(ipArray);
            }else if(legalIps instanceof String){
                String ip = (String) legalIps;
                String[] ipArray = {ip};
                server.setLegalIP(ipArray);
            }

            Object legalIpSection = serverMap.get("legal-ip-section");
            if(legalIpSection instanceof List){
                List<String> ipList = (List<String>) legalIpSection;
                String[] ipArray = new String[ipList.size()];
                int i = 0;
                for (String ip : ipList) {
                    ipArray[i++] = get(ip).toString().trim();
                }
                server.setLegalIpSection(ipArray);
            }else if(legalIpSection instanceof String){
                String ip = (String) legalIpSection;
                String[] ipArray = {ip};
                server.setLegalIpSection(ipArray);
            }

        }
    }
}
