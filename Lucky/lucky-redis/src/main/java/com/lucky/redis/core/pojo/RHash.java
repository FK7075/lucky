package com.lucky.redis.core.pojo;

import com.lucky.redis.core.RedisKey;
import com.lucky.utils.reflect.ClassUtils;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis-Hash
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/29 2:58 下午
 */
public abstract class RHash<K,V> extends RedisKey<K,V> implements Iterable<Map.Entry<K,V>> {

    public RHash(){
        super();
    }

    public RHash(String key) {
        super(key);
    }

    public RHash(int dbNubmer, String key) {
        super(dbNubmer, key);
    }

    @Override
    public void initGeneric() {
        keyType = ClassUtils.getGenericType(this.getClass().getGenericSuperclass())[0];
        valueType = ClassUtils.getGenericType(this.getClass().getGenericSuperclass())[1];
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * @param field
     * @param increment
     * @return
     */
    public Long hincrBy(K field,Long increment){
        return jedis.hincrBy(key,keySerialization(field),increment);
    }

    /**
     * 为哈希表 key 中的指定字段的浮点数值加上增量 increment
     * @param field
     * @param increment
     * @return
     */
    public Double hincrByFloat(K field,double increment){
        return jedis.hincrByFloat(key,keySerialization(field),increment);
    }

    @Override
    public void setKey(String newKey) {
        this.key = "RHash<"+ keyType.getTypeName()+","+valueType.getTypeName()+">-["+newKey+"]";
        key=key.replaceAll(" ","");
    }

    /**
     * 将哈希表中的字段 field 的值设为 value
     * @param field key字段
     * @param pojo 值
     * @return
     */
    public Long hset(K field, V pojo){
        return jedis.hset(key,keySerialization(field),valueSerialization(pojo));
    }

    /**
     * 获取存储在哈希表中指定字段的值。
     * @param field key
     * @return
     */
    public V hget(K field){
        return valueDeserialization(jedis.hget(key,keySerialization(field)));
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在。
     * @param field key
     * @return
     */
    public boolean hexists(K field){
        return jedis.hexists(key,keySerialization(field));
    }

    /**
     * 删除一个或多个哈希表字段
     * @param fields 要删除字段的keys
     */
    public Long hdel(K...fields){
        String[] fieldStrs=new String[fields.length];
        for (int i = 0,j=fields.length; i < j; i++) {
            fieldStrs[i]=keySerialization(fields[i]);
        }
        return jedis.hdel(key,fieldStrs);
    }

    /**
     * 获取所有哈希表中的字段
     * @return
     */
    public Set<K> hkeys(){
        Set<K> keySet= new HashSet<>();
        Set<String> strKeys = jedis.hkeys(key);
        for (String strKey : strKeys) {
            keySet.add(keyDeserialization(strKey));
        }
        return keySet;
    }

    /**
     * 获取hash表中元素的个数
     * @return
     */
    public Long size(){
        return jedis.hlen(key);
    }

    /**
     * 获取所有给定字段的值
     * @param fields
     * @return
     */
    public List<V> hmget(K...fields){
        String[] fieldStrs=new String[fields.length];
        for (int i = 0,j=fields.length; i < j; i++) {
            fieldStrs[i]=keySerialization(fields[i]);
        }
        return jedis.hmget(key,fieldStrs).stream().map((k)->{
            V pojo= valueDeserialization(k);
            return pojo;
        }).collect(Collectors.toList());
    }

    /**
     *同时将多个 field-value (域-值)对设置到哈希表 key 中。
     * @param map
     */
    public String hmset(Map<K,V> map){
        Map<String,String> strMap=new HashMap<>();
        for(Map.Entry<K,V> entry:map.entrySet()){
            strMap.put(keySerialization(entry.getKey()),valueSerialization(entry.getValue()));
        }
        return jedis.hmset(key,strMap);
    }

    /**
     * 获取哈希表中所有值。
     * @return
     */
    public List<V> hvals(){
        return jedis.hvals(key).stream().map((p)->{
            V pojo = valueDeserialization(p);
            return pojo;
        }).collect(Collectors.toList());
    }

    /**
     * 只有在字段 field 不存在时，设置哈希表字段的值。
     * @param field
     * @param pojo
     * @return
     */
    public Long hsetnx(K field, V pojo){
        return jedis.hsetnx(key,keySerialization(field),valueSerialization(pojo));
    }

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     * @return
     */
    public Map<K,V> hgetall(){
        Map<K,V> kvMap=new HashMap<>();
        Map<String, String> kvStrMap = jedis.hgetAll(key);
        for(Map.Entry<String,String> entry:kvStrMap.entrySet()){
            kvMap.put(keyDeserialization(entry.getKey()), valueDeserialization(entry.getValue()));
        }
        return kvMap;
    }

    /**
     * 获取一个用于遍历该hash的迭代器
     * @param cursor 游标
     * @return
     */
    public ScanResult<Map.Entry<K,V>> hscan(String cursor){
        return hscan(cursor,new ScanParams());
    }

    /**
     * 获取一个用于遍历该hash的迭代器
     * @param cursor 游标
     * @param params
     * @return
     */
    public ScanResult<Map.Entry<K,V>> hscan(String cursor, ScanParams params){
        ScanResult<Map.Entry<String, String>> hscan = jedis.hscan(key, cursor,params);
        List<Map.Entry<String, String>> result = hscan.getResult();
        List<Map.Entry<K, V>> results = new ArrayList<Map.Entry<K, V>>();
        for (Map.Entry<String, String> entry : result) {
            results.add(new AbstractMap.SimpleEntry<K, V>( keyDeserialization(entry.getKey()),valueDeserialization(entry.getValue())));
        }
        return new ScanResult<Map.Entry<K,V>>(cursor,results);
    }


    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return hscan(ScanParams.SCAN_POINTER_START).getResult().iterator();
    }
}
