package com.lucky.redis.core;


import com.lucky.redis.core.pojo.RKey;
import com.lucky.redis.serialization.RedisSerialization;
import com.lucky.utils.dm5.MD5Utils;
import redis.clients.jedis.Jedis;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/10 下午12:27
 */
public abstract class RedisKey<K,V> extends RedisSerialization<K,V> {

    public static final String DEL="REDIS-LUCKY-@XFL@FK$*#*#";
    protected static JedisFactory jedisFactory;
    protected String key;
    protected Jedis jedis;
    private final RKey rKey;

    public RedisKey(){
        super();
        initGeneric();
        initFactory();
        jedis=jedisFactory.getJedis();
        rKey=new RKey(jedis);
    }

    public RedisKey(String key){
        super();
        initGeneric();
        initKey(key);
        initFactory();
        jedis=jedisFactory.getJedis();
        rKey=new RKey(jedis);
    }

    public static void setJedisFactory(JedisFactory factory){
        jedisFactory=factory;
    }

    public RedisKey(int dbNubmer,String key) {
        this(key);
        jedis.select(dbNubmer);
    }

    private void initFactory(){
        if(jedisFactory==null){
            jedisFactory=new DefaultJedisFactory();
        }
    }

    private void formatKey(){
        if(key !=null){
            key = MD5Utils.md5(key);
        }
    }

    public RKey getRKey() {
        return rKey;
    }

    /**
     * 该命令用于在 key 存在时删除 key。
     * @param redisKeys
     * @return
     */
    public Long del(RedisKey...redisKeys){
        return rKey.del(key);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持。
     * @return
     */
    public Long persist(){
       return rKey.persist(key);
    }


    /**
     *以毫秒为单位返回 key 的剩余的过期时间。
     * @return
     */
    public Long pttl(){
        return rKey.pttl(key);
    }

    /**
     *以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     * @return
     */
    public Long ttl(){
        return rKey.ttl(key);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中。
     * @param dbNumber (0-15)
     * @return
     */
    public Long mover(int dbNumber){
        return rKey.mover(key,dbNumber);
    }

    /**
     * 为给定 key 设置过期时间，以秒计
     * @param seconds
     */
    public void expire(int seconds){
        rKey.expire(key,seconds);
    }


    /**
     * 设置 key 的过期时间以毫秒计。
     * @param millisecond
     */
    public void pexpire(int millisecond){
        rKey.pexpire(key,millisecond);
    }

    /**
     * 为给定 key 设置过期时间，时间参数是 UNIX 时间戳。
     * @param unixTime
     */
    public void expireAt(long unixTime){
        rKey.expireAt(key,unixTime);
    }

    /**
     * 设置 key 过期时间的时间戳(unix timestamp) 以毫秒计
     * @param millisecondsTimestamp
     */
    public void pexpireAt(long millisecondsTimestamp){
        rKey.pexpireAt(key,millisecondsTimestamp);
    }

    /**
     * 重命名key
     * @param newKey
     * @return
     */
    public String rename(String newKey){
        String oldKey=getKey();
        setKey(newKey);
        formatKey();
        return jedis.rename(oldKey,getKey());
    }

    /**
     * 仅当 newkey 不存在时，将 key 改名为 newkey 。
     * @param newKey
     * @return
     */
    public Long renamenx(String newKey){
        String oldKey=getKey();
        setKey(newKey);
        formatKey();
        return jedis.renamenx(oldKey,getKey());
    }

    public void initKey(String key){
        setKey(key);
        formatKey();
    }

    public abstract void initGeneric();

    public abstract void setKey(String rKey);

    public String getKey() {
        return key;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }


}
