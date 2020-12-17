package com.lucky.jacklamb.cache;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 9:41
 */
public interface Cache<K,V> {

    V get(K key);

    V put(K key,V value);

    boolean containsKey(K key);

    void clear();
}
