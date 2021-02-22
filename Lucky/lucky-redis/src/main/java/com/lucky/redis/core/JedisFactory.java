package com.lucky.redis.core;

import redis.clients.jedis.Jedis;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/10 下午12:32
 */
public interface JedisFactory {

    Jedis getJedis();
}
