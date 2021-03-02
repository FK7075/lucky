package com.lucky.mybatis.conf;

import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.MapConfigAnalysis;
import com.lucky.utils.reflect.ClassUtils;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.logging.log4j2.Log4j2AbstractLoggerImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.log4j2.Log4j2LoggerImpl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.plugin.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Class<? extends VFS> vfsImpl;
    private List<Interceptor> interceptors;
    private final static Map<String,Class<?extends Log>> logImplMap;

    static {
        logImplMap=new HashMap<>(9);
        logImplMap.put("STDOUT_LOGGING", StdOutImpl.class);
        logImplMap.put("SLF4J", Slf4jImpl.class);
        logImplMap.put("NO_LOGGING",NoLoggingImpl.class);
        logImplMap.put("LOG4J", Log4jImpl.class);
        logImplMap.put("LOG4J2_LOGGING", Log4j2LoggerImpl.class);
        logImplMap.put("LOG4j2", Log4j2Impl.class);
        logImplMap.put("LOG4J2_ABSTRACT_LOGGING", Log4j2AbstractLoggerImpl.class);
        logImplMap.put("JDK14_LOGGING", Jdk14LoggingImpl.class);
        logImplMap.put("JDK_ARTA_COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
    }

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

    public void setLogImpl(String logImpl){
        Class<? extends Log> logImplClass = logImplMap.get(logImpl);
        logImplClass= Assert.isNull(logImplClass)? (Class<? extends Log>) ClassUtils.getClass(logImpl) :logImplClass;
        setLogImpl(logImplClass);
    }

    public Class<? extends VFS> getVfsImpl() {
        return vfsImpl;
    }

    public void setVfsImpl(Class<? extends VFS> vfsImpl) {
        this.vfsImpl = vfsImpl;
        VFS.addImplClass(vfsImpl);
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void addInterceptor(Interceptor interceptor){
        this.interceptors.add(interceptor);
    }

    public void addInterceptor(Class<? extends Interceptor> interceptorClass){
        addInterceptor(ClassUtils.newObject(interceptorClass));
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void setLogImpl(Class<? extends Log> logImpl) {
        this.logImpl = logImpl;
    }

    private MybatisConfig(){
        interceptors=new ArrayList<>();
    }

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
        mybatisConfig = defaultMybatisConfig();
        if(mybatisConfig.isFirst()){
            YamlParsing.loadMyBatis(mybatisConfig);
        }
        return mybatisConfig;
    }


    @Override
    public void loadYaml() {
        YamlParsing.loadMyBatis(mybatisConfig);
    }
}
