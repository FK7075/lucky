package com.lucky.mybatis.conf;

import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.utils.config.MapConfigAnalysis;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/7 0007 15:48
 */
public class MybatisConfig extends LuckyConfig {

    private static MybatisConfig mybatisConfig;
    private String mapperLocations;
    private String typeAliasesPackage;
    private boolean mapUnderscoreToCamelCase;
    private boolean autoCommit;
    private Class<? extends Log> logImpl;

    public String getMapperLocations() {
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

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Class<? extends Log> getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(Class<? extends Log> logImpl) {
        this.logImpl = logImpl;
    }

    private MybatisConfig(){};

    public static MybatisConfig defaultMybatisConfig(){
        if(mybatisConfig==null){
            mybatisConfig=new MybatisConfig();
            mybatisConfig.setLogImpl(NoLoggingImpl.class);
            mybatisConfig.setMapUnderscoreToCamelCase(false);
            mybatisConfig.setAutoCommit(false);
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
