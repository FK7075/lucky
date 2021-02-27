package com.lucky.redis.core.pojo;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/24 下午11:54
 */
public class RedisCore<K,V> {

    public  RList<V> getRList(String key){
        return new RList<V>(key) {};
    }

    public RHash<K,V> getRHash(String key){
        return new RHash<K, V>(key) {};
    }
}
