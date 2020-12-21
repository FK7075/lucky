package com.lucky.thymeleaf.conf;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    public static void loadThymeleaf(ThymeleafConfig conf){
        YamlConfAnalysis yml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yml)){
            load(yml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static void load(Map<String,Object> config,ThymeleafConfig thymeleaf) {
        if (config.containsKey("lucky")) {
            Object luckyNode = config.get("lucky");
            if(luckyNode instanceof Map) {
                Map<String, Object> luckyMap = (Map<String, Object>) luckyNode;
                if(luckyMap.containsKey("thymeleaf")) {
                    Object thymeleafNode = luckyMap.get("thymeleaf");
                    if(thymeleafNode instanceof Map){
                        Map<String,Object> thymeleafMap= (Map<String, Object>) thymeleafNode;
                        if(thymeleafMap.containsKey("enabled")){
                            thymeleaf.setEnabled((boolean)thymeleafMap.get("enabled"));
                        }

                        if(thymeleafMap.containsKey("cache")){
                            thymeleaf.setCache((boolean)thymeleafMap.get("cache"));
                        }

                        if(thymeleafMap.containsKey("encoding")){
                            thymeleaf.setEncoding(thymeleafMap.get("encoding").toString());
                        }

                        if(thymeleafMap.containsKey("prefix")){
                            thymeleaf.setPrefix(thymeleafMap.get("prefix").toString());
                        }

                        if(thymeleafMap.containsKey("mode")){
                            thymeleaf.setModel(thymeleafMap.get("mode").toString());
                        }

                        if(thymeleafMap.containsKey("suffix")){
                            thymeleaf.setSuffix(thymeleafMap.get("suffix").toString());
                        }
                    }
                }
            }
        }
    }
}
