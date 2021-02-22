package com.lucky.redis.core.pojo;

import com.lucky.redis.core.RedisKey;
import com.lucky.utils.annotation.NonNull;
import com.lucky.utils.reflect.ClassUtils;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis-Set
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/29 3:05 下午
 */
public abstract class RSet<T> extends RedisKey<String,T> implements Iterable<T>{

    public RSet(){
        super();
    }

    public RSet(String key) {
        super(key);
    }

    public RSet(int seconds, String key) {
        super(seconds, key);
    }

    @Override
    public void initGeneric() {
        keyType=String.class;
        valueType= ClassUtils.getGenericType(this.getClass().getGenericSuperclass())[0];
    }

    @Override
    public void setKey(String key) {
        this.key = "RSet<"+valueType.getTypeName()+">-["+key+"]";
        this.key=this.key.replaceAll(" ","");
    }

    /**
     * 向集合添加一个或多个成员
     * @param members 待添加的元素
     * @return
     */
    public Long sadd(T...members){
        String[] strSet=new String[members.length];
        for (int i = 0; i < members.length; i++) {
            strSet[i]=valueSerialization(members[i]);
        }
        return jedis.sadd(key,strSet);
    }

    /**
     * 获取集合的成员数
     * @return
     */
    public Long size(){
        return jedis.scard(key);
    }

    @SafeVarargs
    private final String[] getKeys(RSet<T>... otherSets){
        String[] otherKey=new String[otherSets.length+1];
        otherKey[0]=key;
        int index=1;
        for (RSet<T> otherSet : otherSets) {
            otherKey[index]=otherSet.getKey();
        }
        return otherKey;
    }

    private Set<T> getSet(Set<String> stringSet){
        Set<T> pojoSet=new HashSet<>();
        for (String json:stringSet){
            pojoSet.add(valueDeserialization(json));
        }
        return pojoSet;
    }

    /**
     * 返回本集合与其他集合之间的差异。
     * @param otherSets 其他集合
     * @return
     */
    public Set<T> sdiif(RSet<T>...otherSets){
        return getSet(jedis.sdiff(getKeys(otherSets)));
    }

    /**
     * 返回本集合与给定所有集合的差集并存储在 destination集合 中
     * @param destination 差异存储集合
     * @param otherSets 其他对比集合
     * @return
     */
    public Long sdiifstore(RSet<T> destination, RSet<T>...otherSets){
        return jedis.sdiffstore(destination.getKey(),getKeys(otherSets));
    }

    /**
     * 返回本集合与给定所有集合的交集
     * @param otherSets 其他对比集合
     * @return
     */
    public Set<T> sinter(RSet<T>...otherSets){
        return getSet(jedis.sinter(getKeys(otherSets)));
    }

    /**
     * 返回本集合与给定所有集合的交集并存储在 destination集合中
     * @param destination 差异存储集合
     * @param otherSets 其他对比集合
     * @return
     */
    public Long sinterstore(RSet<T> destination, RSet<T>...otherSets){
        return jedis.sinterstore(destination.getKey(),getKeys(otherSets));
    }

    /**
     * 判断pojo元素是否是该集合的成员
     * @param pojo 待判断的元素
     * @return
     */
    public boolean sismember(T pojo){
        return jedis.sismember(key,valueSerialization(pojo));
    }

    /**
     * 返回集合中的所有成员
     * @return
     */
    public Set<T> smembers(){
        return getSet(jedis.smembers(key));
    }

    /**
     * 将pojo元素从本合移动到 destination集合
     * @param destination 存储集合
     * @param pojo 待移动的元素
     * @return
     */
    public Long smove(RSet<T> destination, T pojo){
        if(sismember(pojo)){
            return jedis.smove(key,destination.getKey(),valueSerialization(pojo));
        }
        throw new RuntimeException("集合\""+key+"\"中不存在该元素:["+pojo+"]，无法移动！");
    }

    /**
     * 移除并返回集合中的一个随机元素
     * @return
     */
    public T spop(){
        return valueDeserialization(jedis.spop(key));
    }

    /**
     * 移除并返回集合中的一组随机元素
     * @param count 元素个数
     * @return
     */
    public Set<T> spop(long count){
        return getSet(jedis.spop(key,count));
    }

    /**
     * 返回集合中的一个随机元素
     * @return
     */
    public T srandmember(){
        return valueDeserialization(jedis.srandmember(key));
    }

    /**
     * 返回集合中的一组随机元素
     * @return
     */
    public List<T> srandmember(int count){
        List<T> pojoList=new ArrayList<>();
        List<String> jsonList = jedis.srandmember(key, count);
        return jsonList.stream().map((j)->{
           T pojo= valueDeserialization(j);
           return pojo;
        }).collect(Collectors.toList());
    }

    /**
     * 移除集合中一个或多个成员
     * @param pojos
     * @return
     */
    public Long srem(T...pojos){
        String[] jsonT=new String[pojos.length];
        for (int i = 0,j=pojos.length; i <j ; i++) {
            jsonT[i]=valueSerialization(pojos[i]);
        }
        return jedis.srem(key,jsonT);
    }

    /**
     * 返回本集合与所有给定集合的并集
     * @param otherSets
     * @return
     */
    public Set<T> sunion( RSet<T>...otherSets){
        return getSet(jedis.sunion(getKeys(otherSets)));
    }

    /**
     * 返回本集合与所有给定集合的并集,并将所有元素放入destination集合中
     * @param destination 存储集合
     * @param otherSets  其他集合
     * @return
     */
    public Long sunionstore(RSet<T> destination, RSet<T>...otherSets){
        return jedis.sunionstore(destination.getKey(),getKeys(otherSets));
    }

    /**
     * 迭代集合中的元素
     * @param cursor 游标
     * @return
     */
    public ScanResult<T> sscan(String cursor){
        return sscan(cursor,new ScanParams());
    }

    /**
     * 迭代集合中的元素
     * @param cursor 游标
     * @param scanParams
     * @return
     */
    public ScanResult<T> sscan(String cursor, ScanParams scanParams){
        ScanResult<String> sscan = jedis.sscan(key, cursor,scanParams);
        List<T> pojoList=sscan.getResult().stream().map((j)->{
            T pojo= valueDeserialization(j);
            return pojo;
        }).collect(Collectors.toList());
        return new ScanResult<T>(sscan.getCursor(),pojoList);
    }

    /**
     * 关闭Redis连接
     */
    public void close(){
        jedis.close();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return sscan(ScanParams.SCAN_POINTER_START).getResult().iterator();
    }
}
