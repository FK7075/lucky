package com.lucky.redis.core.pojo;

import com.lucky.redis.core.RedisKey;
import com.lucky.utils.reflect.ClassUtils;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

import java.util.*;

/**
 * Redis-Sorted Set
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/30 4:57 上午
 */
public abstract class ZSet<T> extends RedisKey<String,T> implements Iterable<ZSet.Entity<T>>{

    public ZSet(){
        super();
    }

    public ZSet(String key) {
        super(key);
    }

    public ZSet(int dbNubmer,String key) {
        super(dbNubmer,key);
    }

    @Override
    public void initGeneric() {
        keyType=String.class;
        valueType= ClassUtils.getGenericType(this.getClass().getGenericSuperclass())[0];
    }

    @Override
    public void setKey(String key) {
        this.key = "ZSet<"+valueType.getTypeName()+">-["+key+"]";
        this.key=this.key.replaceAll(" ","");
    }

    /**
     * 向有序集合添加或者更新已存在成员的分数
     * @param score 分数
     * @param pojo 待添加的元素
     * @return
     */
    public Long zadd(double score,T pojo){
        return jedis.zadd(key, score, valueSerialization(pojo));
    }

    /**
     * 向有序集合添加或者更新已存在成员的分数
     * @param score 分数
     * @param pojo 待添加的元素
     * @param params 其他参数
     * @return
     */
    public Long zadd(double score, T pojo, ZAddParams params){
        return jedis.zadd(key, score, valueSerialization(pojo),params);
    }

    /**
     * 向有序集合添加或者更新[多个]已存在成员的分数
     * @param zmap
     * @return
     */
    public Long zadd(Map<T,Double> zmap){
        Map<String,Double> jmap=new HashMap<>();
        for(Map.Entry<T,Double> entry:zmap.entrySet()){
            jmap.put(valueSerialization(entry.getKey()),entry.getValue());
        }
        return jedis.zadd(key,jmap);
    }

    /**
     * 向有序集合添加或者更新[多个]已存在成员的分数
     * @param zmap 元素与分数组成的Map
     * @param zAddParams 其他参数
     * @return
     */
    public Long zadd(Map<T,Double> zmap,ZAddParams zAddParams ){
        Map<String,Double> jmap=new HashMap<>();
        for(Map.Entry<T,Double> entry:zmap.entrySet()){
            jmap.put(valueSerialization(entry.getKey()),entry.getValue());
        }
        return jedis.zadd(key,jmap,zAddParams);
    }

    /**
     * 计算在有序集合中指定区间分数的成员数
     * @param min 下限
     * @param max 上限
     * @return
     */
    public Long zcount(double min,double max){
        return jedis.zcount(key,min, max);
    }

    /**
     * 计算在有序集合中指定区间分数的成员数
     * @param min 下限
     * @param max 上限
     * @return
     */
    public Long zcount(String min,String max){
        return jedis.zcount(key,min, max);
    }

    /**
     * 有序集合中对指定成员的分数加上增量 increment
     * @param increment 分数增量
     * @param pojo 元素
     * @return
     */
    public Double zincrby(double increment,T pojo){
        return jedis.zincrby(key, increment, valueSerialization(pojo));
    }

    /**
     * 有序集合中对指定成员的分数加上增量 increment
     * @param increment 分数增量
     * @param pojo 元素
     * @param zIncrByParams 其他参数
     * @return
     */
    public Double zincrby(double increment, T pojo, ZIncrByParams zIncrByParams){
        return jedis.zincrby(key, increment, valueSerialization(pojo),zIncrByParams);
    }

    /**
     * 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 deSet 中
     * @param deSet 存储计算结果的集合
     * @param pojoZSet 其他参与运算的集合
     * @return
     */
    public Long zinterstore(ZSet<T> deSet, ZSet<T>...pojoZSet){
        String[] others=new String[pojoZSet.length+1];
        others[0]=key;
        for (int i = 1,j=pojoZSet.length; i <j ; i++) {
            others[i]=pojoZSet[i-1].getKey();
        }
        return jedis.zinterstore(deSet.getKey(), others);
    }

    /**
     * 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 deSet 中
     * @param deSet 存储计算结果的集合
     * @param zParams 其他参数
     * @param pojoZSet 其他参与运算的集合
     * @return
     */
    public Long zinterstore(ZSet<T> deSet, ZParams zParams, ZSet<T>...pojoZSet){
        String[] others=new String[pojoZSet.length+1];
        others[0]=key;
        for (int i = 1,j=pojoZSet.length; i <j ; i++) {
            others[i]=pojoZSet[i-1].getKey();
        }
        return jedis.zinterstore(deSet.getKey(),zParams, others);
    }

    /**
     * 在有序集合中计算指定字典区间内成员数量
     * @param min
     * @param max
     * @return
     */
    public Long zlexcount(String min,String max){
        return jedis.zlexcount(key,min, max);
    }

    /**
     * 通过索引区间返回有序集合指定区间内的成员
     * @param start
     * @param stop
     * @return
     */
    public Set<T> zrange(long start, long stop){
        Set<String> zrange = jedis.zrange(key, start, stop);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : zrange) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过字典区间返回有序集合的成员
     * @param min
     * @param max
     * @return
     */
    public Set<T> zrangeByLex(String min,String max){
        Set<String> strings = jedis.zrangeByLex(key, min, max);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过字典区间返回有序集合的成员(LIMIT)
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<T> zrangeByLex(String min,String max,int offset,int count){
        Set<String> strings = jedis.zrangeByLex(key, min, max,offset,count);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过分数返回有序集合指定区间内的成员
     * @param min
     * @param max
     * @return
     */
    public Set<T> zrangeByScore(double min,double max){
        Set<String> strings = jedis.zrangeByScore(key, min, max);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过分数返回有序集合指定区间内的成员
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<T> zrangeByScore(double min,double max,int offset,int count){
        Set<String> strings = jedis.zrangeByScore(key, min, max,offset,count);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过分数返回有序集合指定区间内的成员
     * @param min
     * @param max
     * @return
     */
    public Set<T> zrangeByScore(String min,String max){
        Set<String> strings = jedis.zrangeByScore(key, min, max);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 通过分数返回有序集合指定区间内的成员
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<T> zrangeByScore(String min,String max,int offset,int count){
        Set<String> strings = jedis.zrangeByScore(key, min, max,offset,count);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集合中指定成员的索引
     * @param pojo
     * @return
     */
    public Long zrank(T pojo){
        return jedis.zrank(key, valueSerialization(pojo));
    }

    /**
     * 移除有序集合中的一个或多个成员
     * @param pojos
     * @return
     */
    public Long zrem(T...pojos){
        String[] jsonT=new String[pojos.length];
        for (int i = 0,j=pojos.length; i < j; i++) {
            jsonT[i]=valueSerialization(pojos[i]);
        }
        return jedis.zrem(key, jsonT);
    }

    /**
     * 移除有序集合中给定的字典区间的所有成员
     * @param min
     * @param max
     * @return
     */
    public Long zremrangeByLex(String min,String max){
        return jedis.zremrangeByLex(key, min, max);
    }

    /**
     * 移除有序集合中给定的排名区间的所有成员
     * @param start
     * @param stop
     * @return
     */
    public Long zremrangeByRank(long start,long stop){
        return jedis.zremrangeByRank(key, start, stop);
    }

    /**
     * 移除有序集合中给定的分数区间的所有成员
     * @param min
     * @param max
     * @return
     */
    public Long zremrangeByScore(double min,double max){
        return jedis.zremrangeByScore(key, min, max);
    }

    /**
     * 返回有序集中指定区间内的成员，通过索引，分数从高到低
     * @param start
     * @param stop
     * @return
     */
    public Set<T> zrevrange(long start,long stop){
        Set<String> strings = jedis.zrevrange(key, start, stop);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集中指定分数区间内的成员，分数从高到低排序
     * @param min
     * @param max
     * @return
     */
    public Set<T> zrevrangeByScore(String min,String max){
        Set<String> strings = jedis.zrevrangeByScore(key,max,min);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集中指定分数区间内的成员，分数从高到低排序(LIMIT)
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<T> zrevrangeByScore(String min,String max,int offset,int count){
        Set<String> strings = jedis.zrevrangeByScore(key,max,min,offset,count);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集中指定分数区间内的成员，分数从高到低排序
     * @param min
     * @param max
     * @return
     */
    public Set<T> zrevrangeByScore(double min,double max){
        Set<String> strings = jedis.zrevrangeByScore(key,max,min);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集中指定分数区间内的成员，分数从高到低排序(LIMIT)
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<T> zrevrangeByScore(double min,double max,int offset,int count){
        Set<String> strings = jedis.zrevrangeByScore(key,max,min,offset,count);
        Set<T> pojoSte=new HashSet<>();
        for (String jp : strings) {
            pojoSte.add(valueDeserialization(jp));
        }
        return pojoSte;
    }

    /**
     * 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
     * @param pojo
     * @return
     */
    public Long zrevrank(T pojo){
        return jedis.zrevrank(key, valueSerialization(pojo));
    }

    /**
     * 返回有序集中，成员的分数值
     * @param pojo
     * @return
     */
    public Double zscore(T pojo){
        return jedis.zscore(key,valueSerialization(pojo));
    }

    /**
     * 计算给定的一个或多个有序集的并集，并存储在新的 destination 中
     * @param destination
     * @param pojoSets
     * @return
     */
    public Long zunionstore(ZSet<T> destination, ZSet<T>...pojoSets){
        String[] keys=new String[pojoSets.length+1];
        keys[0]=key;
        for (int i = 1,j=keys.length; i < j; i++) {
            keys[i]=pojoSets[i-1].getKey();
        }
        return jedis.zunionstore(destination.getKey(),keys);
    }

    /**
     * 计算给定的一个或多个有序集的并集，并存储在新的 destination 中
     * @param destination
     * @param params
     * @param pojoSets
     * @return
     */
    public Long zunionstore(ZSet<T> destination, ZParams params, ZSet<T>...pojoSets){
        String[] keys=new String[pojoSets.length+1];
        keys[0]=key;
        for (int i = 1,j=keys.length; i < j; i++) {
            keys[i]=pojoSets[i-1].getKey();
        }
        return jedis.zunionstore(destination.getKey(),params,keys);
    }

    /**
     * 迭代有序集合中的元素（包括元素成员和元素分值）
     * @param cursor
     * @return
     */
    public  ScanResult<Tuple> zscan(String cursor){
        return jedis.zscan(key, cursor);
    }

    /**
     *  迭代有序集合中的元素（包括元素成员和元素分值）
     * @param cursor
     * @param scanParams
     * @return
     */
    public  ScanResult<Tuple> zscan(String cursor, ScanParams scanParams){
        return jedis.zscan(key, cursor,scanParams);
    }

    /**
     * 获取有序集合的成员数
     * @return
     */
    public long size(){
        return jedis.zcard(key);
    }

    @Override
    public Iterator<Entity<T>> iterator() {
        return zscan(ScanParams.SCAN_POINTER_START).getResult()
                .stream()
                .map(t->new Entity<T>(t.getScore(),valueDeserialization(t.getElement()),t.getBinaryElement()))
                .iterator();
    }

   public static class Entity<T>{
        private Double score;
        private T element;
        private byte[] byteElement;

       public Entity(Double score, T element, byte[] byteElement) {
           this.score = score;
           this.element = element;
           this.byteElement = byteElement;
       }

       public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

       public byte[] getByteElement() {
           return byteElement;
       }

       public void setByteElement(byte[] byteElement) {
           this.byteElement = byteElement;
       }
   }

}
