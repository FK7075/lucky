package com.lucky.mybatis.conf;

import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.utils.config.MapConfigAnalysis;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/7 0007 15:48
 */
public class MybatisConfig extends LuckyConfig {

    private static MybatisConfig mybatisConfig;
    private String mapperLocations;
    private String typeAliasesPackage;

    public String getMapperLocations() {
        if(mapperLocations.startsWith("classpath:")){
            mapperLocations=mapperLocations.substring(10);
        }
        mapperLocations=mapperLocations.startsWith("/")?mapperLocations:"/"+mapperLocations;
        mapperLocations=mapperLocations.endsWith("/")?mapperLocations:mapperLocations+"/";
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    private MybatisConfig(){};

    public static MybatisConfig defaultMybatisConfig(){
        if(mybatisConfig==null){
            mybatisConfig=new MybatisConfig();
            mybatisConfig.setMapperLocations("classpath:/mapper/");
            mybatisConfig.setFirst(true);
        }
        return mybatisConfig;
    }

    public static MybatisConfig getMybatisConfig(){
        MybatisConfig serverConfig = defaultMybatisConfig();
        if(serverConfig.isFirst()){
            YamlParsing.loadMyBatis(serverConfig);
        }
        return serverConfig;
    }


}
