package com.lucky.email.conf;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    private static final YamlConfAnalysis yaml = ConfigUtils.getYamlConfAnalysis();

    public static void loadEmail(EmailConfig conf){
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }
    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String,Object> config,EmailConfig email){
        if(!config.containsKey("email")){
            throw new RuntimeException();
        }
        Object emailNode = config.get("email");
        if(emailNode instanceof Map){
            Map<String,Object> emailMap= (Map<String, Object>) emailNode;
            if(emailMap.containsKey("smtp-host")){
                email.setSmtpHost(get(emailMap.get("smtp-host")).toString());
            }
            if(emailMap.containsKey("smtp-port")){
                Object smtpPort = get(emailMap.get("smtp-port"));
                if(smtpPort instanceof Integer){
                    email.setSmtpPort((Integer) smtpPort);
                }else{
                    email.setSmtpPort((int) JavaConversion.strToBasic(smtpPort.toString(),int.class));
                }
            }
            if(emailMap.containsKey("pop-host")){
                email.setPopHost(get(emailMap.get("pop-host")).toString());
            }
            if(emailMap.containsKey("pop-port")){
                Object popPort = get(emailMap.get("pop-port"));
                if(popPort instanceof Integer){
                    email.setPopPort((Integer) popPort);
                }else{
                    email.setPopPort((int) JavaConversion.strToBasic(popPort.toString(),int.class));
                }
            }
            if(emailMap.containsKey("username")){
                email.setEmail(get(emailMap.get("username")).toString());
            }
            if(emailMap.containsKey("alias")){
                email.setUsername(get(emailMap.get("alias")).toString());
            }
            if(emailMap.containsKey("password")){
                email.setPassword(get(emailMap.get("password")).toString());
            }
            if(emailMap.containsKey("smtp-auth")){
                Object smtpAuth = get(emailMap.get("smtp-auth"));
                if(smtpAuth instanceof Boolean){
                    email.setSmtpAuth((Boolean) smtpAuth);
                }else{
                    email.setSmtpAuth((Boolean) JavaConversion.strToBasic(smtpAuth.toString(),boolean.class));
                }
            }
            if(emailMap.containsKey("smtp-starttls-enable")){
                Object sse = get(emailMap.get("smtp-starttls-enable"));
                if(sse instanceof Boolean){
                    email.setSmtpStarttlsEnable((Boolean) sse);
                }else{
                    email.setSmtpStarttlsEnable((Boolean)JavaConversion.strToBasic(sse.toString(),boolean.class));
                }
            }
            if(emailMap.containsKey("smtp-starttls-required")){
                Object ssr = get(emailMap.get("smtp-starttls-required"));
                if(ssr instanceof Boolean){
                    email.setSmtpStarttlsRequired((Boolean) ssr);
                }else{
                    email.setSmtpStarttlsRequired((Boolean)JavaConversion.strToBasic(ssr.toString(),boolean.class));
                }
            }
        }
    }
}
