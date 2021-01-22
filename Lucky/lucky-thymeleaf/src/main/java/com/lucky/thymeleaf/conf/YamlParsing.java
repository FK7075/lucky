package com.lucky.thymeleaf.conf;

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

    public static void loadThymeleaf(ThymeleafConfig conf){
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
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
                            Object enabled = get(thymeleafMap.get("enabled"));
                            if(enabled instanceof Boolean){
                                thymeleaf.setEnabled((Boolean) enabled);
                            }else{
                                thymeleaf.setEnabled((Boolean) JavaConversion.strToBasic(enabled.toString(),boolean.class));
                            }
                        }
                        if(thymeleafMap.containsKey("cache")){
                            Object cache = get(thymeleafMap.get("cache"));
                            if(cache instanceof Boolean){
                                thymeleaf.setCache((Boolean) cache);
                            }else{
                                thymeleaf.setCache((Boolean)JavaConversion.strToBasic(cache.toString(),boolean.class));
                            }
                        }
                        if(thymeleafMap.containsKey("encoding")){
                            thymeleaf.setEncoding(get(thymeleafMap.get("encoding")).toString());
                        }
                        if(thymeleafMap.containsKey("prefix")){
                            thymeleaf.setPrefix(get(thymeleafMap.get("prefix")).toString());
                        }
                        if(thymeleafMap.containsKey("mode")){
                            thymeleaf.setModel(get(thymeleafMap.get("mode")).toString());
                        }
                        if(thymeleafMap.containsKey("suffix")){
                            thymeleaf.setSuffix(get(thymeleafMap.get("suffix")).toString());
                        }
                    }
                }
            }
        }
    }
}
