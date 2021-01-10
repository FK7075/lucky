package com.lucky.mybatis.conf;

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

    public static void loadMyBatis(MybatisConfig conf){
        YamlConfAnalysis yml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yml)){
            load(yml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static void load(Map<String,Object> config,MybatisConfig conf){
        if(config.containsKey("mybatis")){
            Object mybatisNode = config.get("mybatis");
            if(mybatisNode instanceof Map){
                Map<String,Object> mybatisMap= (Map<String, Object>) mybatisNode;
                if(mybatisMap.containsKey("mapper-locations")){
                    conf.setMapperLocations(mybatisMap.get("mapper-locations").toString());
                }
                if(mybatisMap.containsKey("type-aliases-package")){
                    conf.setTypeAliasesPackage(mybatisMap.get("type-aliases-package").toString());
                }
            }
        }
    }
}
