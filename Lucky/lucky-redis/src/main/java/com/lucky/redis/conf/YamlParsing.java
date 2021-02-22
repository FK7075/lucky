package com.lucky.redis.conf;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/19 下午12:15
 */
public abstract class YamlParsing {

    private static YamlConfAnalysis yaml;

    public static void loadJedis(JedisConfig conf){
        yaml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String, Object> map, JedisConfig conf) {
        Object redis = map.get("redis");
        if(redis instanceof Map){
            Map<String,Object> redisMap= (Map<String, Object>) redis;
            Object dbNumberNode = redisMap.get("db-number");
            if(dbNumberNode instanceof Integer){
                conf.setDbNumber((Integer) dbNumberNode);
            }else if(dbNumberNode!=null){
                conf.setDbNumber((Integer) JavaConversion.strToBasic(get(dbNumberNode).toString(),Integer.class,true));
            }
            Object hostNode = redisMap.get("host");
            if(hostNode!=null){
                conf.setHost(get(hostNode).toString());
            }
            Object portNode = redisMap.get("port");
            if(portNode!=null){
                if(portNode instanceof Integer){
                    conf.setPort((Integer) portNode);
                }else{
                    conf.setPort((Integer) JavaConversion.strToBasic(get(portNode).toString(),Integer.class,true));
                }
            }
            Object passwordNode = redisMap.get("password");
            if(passwordNode!=null){
                conf.setPassword(get(passwordNode).toString());
            }
            Object timeoutNode = redisMap.get("timeout");
            if(timeoutNode !=null){
                if(timeoutNode instanceof Integer){
                    conf.setTimeout((Integer) timeoutNode);
                }else{
                    conf.setTimeout((Integer) JavaConversion.strToBasic(get(timeoutNode).toString(),Integer.class,true));
                }
            }
            Object maxIdleNode = redisMap.get("max-idle");
            if(maxIdleNode !=null){
                if(maxIdleNode instanceof Integer){
                    conf.setMaxIdle((Integer) maxIdleNode);
                }else{
                    conf.setMaxIdle((Integer) JavaConversion.strToBasic(get(maxIdleNode).toString(),Integer.class,true));
                }
            }
            Object maxActiveNode = redisMap.get("max-active");
            if(maxActiveNode !=null){
                if(maxActiveNode instanceof Integer){
                    conf.setMaxActive((Integer) maxActiveNode);
                }else{
                    conf.setMaxActive((Integer) JavaConversion.strToBasic(get(maxActiveNode).toString(),Integer.class,true));
                }
            }
            Object maxTotalNode = redisMap.get("max-total");
            if(maxTotalNode !=null){
                if(maxTotalNode instanceof Integer){
                    conf.setMaxTotal((Integer) maxTotalNode);
                }else{
                    conf.setMaxTotal((Integer) JavaConversion.strToBasic(get(maxTotalNode).toString(),Integer.class,true));
                }
            }
            Object maxWaitMillisNode = redisMap.get("max-wait-millis");
            if(maxWaitMillisNode !=null){
                if(maxWaitMillisNode instanceof Integer){
                    conf.setMaxWaitMillis((Integer) maxWaitMillisNode);
                }else{
                    conf.setMaxWaitMillis((Integer) JavaConversion.strToBasic(get(maxWaitMillisNode).toString(),Integer.class,true));
                }
            }
            Object minEvictableIdleTimeMillisNode = redisMap.get("min-evictable-idle-time-millis");
            if(minEvictableIdleTimeMillisNode !=null){
                if(minEvictableIdleTimeMillisNode instanceof Integer){
                    conf.setMinEvictableIdleTimeMillis((Integer) minEvictableIdleTimeMillisNode);
                }else{
                    conf.setMinEvictableIdleTimeMillis((Integer) JavaConversion.strToBasic(get(minEvictableIdleTimeMillisNode).toString(),Integer.class,true));
                }
            }
            Object numTestsPerEvictionRunNode = redisMap.get("num-tests-per-eviction-run");
            if(numTestsPerEvictionRunNode !=null){
                if(numTestsPerEvictionRunNode instanceof Integer){
                    conf.setNumTestsPerEvictionRun((Integer) numTestsPerEvictionRunNode);
                }else{
                    conf.setNumTestsPerEvictionRun((Integer) JavaConversion.strToBasic(get(numTestsPerEvictionRunNode).toString(),Integer.class,true));
                }
            }
            Object timeBetweenEvictionRunsMillisNode = redisMap.get("time-between-eviction-runs-millis");
            if(timeBetweenEvictionRunsMillisNode !=null){
                if(timeBetweenEvictionRunsMillisNode instanceof Integer){
                    conf.setTimeBetweenEvictionRunsMillis((Integer) timeBetweenEvictionRunsMillisNode);
                }else{
                    conf.setTimeBetweenEvictionRunsMillis((Integer) JavaConversion.strToBasic(get(timeBetweenEvictionRunsMillisNode).toString(),Integer.class,true));
                }
            }
            Object testOnBorrowNode = redisMap.get("test-on-borrow");
            if(testOnBorrowNode!=null){
                if(testOnBorrowNode instanceof Boolean){
                    conf.setTestOnBorrow((Boolean) testOnBorrowNode);
                }else{
                    conf.setTestOnBorrow((Boolean) JavaConversion.strToBasic(get(testOnBorrowNode).toString(),boolean.class));
                }
            }
            Object testWhileIdleNode = redisMap.get("test-while-idle");
            if(testWhileIdleNode!=null){
                if(testWhileIdleNode instanceof Boolean){
                    conf.setTestWhileIdle((Boolean) testWhileIdleNode);
                }else{
                    conf.setTestWhileIdle((Boolean) JavaConversion.strToBasic(get(testWhileIdleNode).toString(),boolean.class));
                }
            }

        }

    }
}
