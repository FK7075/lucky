package com.lucky.redis.core;

import com.lucky.redis.conf.JedisConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/19 下午12:02
 */
public class DefaultJedisFactory implements JedisFactory{

    private static final Logger log= LogManager.getLogger(DefaultJedisFactory.class);
    private static final JedisPool jsp;
    private static final JedisConfig redisConfig;

    static {
        log.info("Redis Start Initialization...");
        redisConfig = JedisConfig.getJedisConfig();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisConfig.getMaxTotal());
        config.setMaxIdle(redisConfig.getMaxIdle());
        config.setMaxWaitMillis(redisConfig.getMaxWaitMillis());
        config.setTestOnBorrow(redisConfig.isTestOnBorrow());
        config.setTestOnReturn(redisConfig.isTestOnReturn());
        if(redisConfig.getPassword()!=null){
            jsp = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(),redisConfig.getTimeout(),redisConfig.getPassword());
        }else{
            jsp = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(),redisConfig.getTimeout());
        }

    }

    @Override
    public Jedis getJedis() {
        Jedis jedis = jsp.getResource();
        jedis.select(redisConfig.getDbNumber());
        return jedis;
    }
}
