package com.lucky.redis.conf;

import com.lucky.framework.confanalysis.LuckyConfig;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/19 下午12:05
 */
public class JedisConfig extends LuckyConfig {

    private static JedisConfig config;
    private JedisConfig(){};

    private int dbNumber=0;
    private String host;
    private int port;
    private String password;
    private int timeout = 2000;
    private int maxIdle = 300;
    private int maxActive = 600;
    private int maxTotal = 1000;
    private int maxWaitMillis = 1000;
    private int minEvictableIdleTimeMillis = 300000;
    private int numTestsPerEvictionRun = 1024;
    private int timeBetweenEvictionRunsMillis = 30000;
    private boolean testOnBorrow = true;
    private boolean testOnReturn =true;
    private boolean testWhileIdle = false;

    public int getDbNumber() {
        return dbNumber;
    }

    public void setDbNumber(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public static JedisConfig defaultJedisConfig(){
        if(config==null){
            config=new JedisConfig();
            config.setFirst(true);
        }
        return config;
    }

    public static JedisConfig getJedisConfig(){
        JedisConfig jedisConfig = defaultJedisConfig();
        if(jedisConfig.isFirst()){
            YamlParsing.loadJedis(jedisConfig);
        }
        return jedisConfig;
    }

}
