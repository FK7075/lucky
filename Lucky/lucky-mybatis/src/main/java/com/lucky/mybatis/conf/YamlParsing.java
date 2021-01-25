package com.lucky.mybatis.conf;

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

    public static void loadMyBatis(MybatisConfig conf){
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String,Object> config,MybatisConfig conf){
        if(config.containsKey("mybatis")){
            Object mybatisNode = config.get("mybatis");
            if(mybatisNode instanceof Map){
                Map<String,Object> mybatisMap= (Map<String, Object>) mybatisNode;
                if(mybatisMap.containsKey("mapper-locations")){
                    conf.setMapperLocations(get(mybatisMap.get("mapper-locations")).toString());
                }
                if(mybatisMap.containsKey("type-aliases-package")){
                    conf.setTypeAliasesPackage(get(mybatisMap.get("type-aliases-package")).toString());
                }
                if(mybatisMap.containsKey("map-underscore-to-camel-case")){
                    Object mutcc = get(mybatisMap.get("map-underscore-to-camel-case"));
                    if(mutcc instanceof Boolean){
                        conf.setMapUnderscoreToCamelCase((Boolean) mutcc);
                    }else{
                        conf.setMapUnderscoreToCamelCase((Boolean) JavaConversion.strToBasic(mutcc.toString(),boolean.class));
                    }
                }
                if(mybatisMap.containsKey("auto-commit")){
                    Object autoCommit = get(mybatisMap.get("auto-commit"));
                    if(autoCommit instanceof Boolean){
                        conf.setAutoCommit((Boolean) autoCommit);
                    }else{
                        conf.setAutoCommit((Boolean) JavaConversion.strToBasic(autoCommit.toString(),boolean.class));
                    }
                }
                if(mybatisMap.containsKey("log-impl")){
                    conf.setLogImpl(get(mybatisMap.get("log-impl")).toString());
                }
            }
        }
    }
}
