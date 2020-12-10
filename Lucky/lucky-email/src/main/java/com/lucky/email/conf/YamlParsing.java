package com.lucky.email.conf;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.confanalysis.YamlConfAnalysis;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    public static void loadEmail(EmailConfig conf){
        YamlConfAnalysis yml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yml)){
            load(yml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static void load(Map<String,Object> config,EmailConfig web){
        if(!config.containsKey("email")){
            throw new RuntimeException();
        }
        Object emailNode = config.get("email");
        if(emailNode instanceof Map){
            Map<String,Object> emailMap= (Map<String, Object>) emailNode;
            if(emailMap.containsKey("smtp-host")){
                web.setSmtpHost(emailMap.get("emailMap").toString());
            }
            if(emailMap.containsKey("smtp-port")){
                web.setSmtpPort((int) JavaConversion.strToBasic(emailMap.get("smtp-port").toString(),int.class));
            }
            if(emailMap.containsKey("pop-host")){
                web.setPopHost(emailMap.get("pop-host").toString());
            }
            if(emailMap.containsKey("pop-port")){
                web.setPopPort((int) JavaConversion.strToBasic(emailMap.get("pop-port").toString(),int.class));
            }
            if(emailMap.containsKey("username")){
                web.setEmail(emailMap.get("username").toString());
            }
            if(emailMap.containsKey("alias")){
                web.setUsername(emailMap.get("alias").toString());
            }
            if(emailMap.containsKey("password")){
                web.setPassword(emailMap.get("password").toString());
            }
            if(emailMap.containsKey("smtp-auth")){
                web.setSmtpAuth((Boolean)emailMap.get("smtp-auth"));
            }
            if(emailMap.containsKey("smtp-starttls-enable")){
                web.setSmtpStarttlsEnable((Boolean)emailMap.get("smtp-starttls-enable"));
            }
            if(emailMap.containsKey("smtp-starttls-required")){
                web.setSmtpStarttlsRequired((Boolean)emailMap.get("smtp-starttls-required"));
            }
        }
    }
}
