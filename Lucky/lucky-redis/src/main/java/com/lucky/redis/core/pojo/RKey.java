package com.lucky.redis.core.pojo;

import com.lucky.redis.core.RedisKey;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.Set;

/**
 * Redis-Key操作
 * @author fk7075
 * @version 1.0
 * @date 2020/8/31 18:33
 */
public class RKey implements Iterable<String>{

    private Jedis jedis;

    public RKey(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 该命令用于在 key 存在时删除 key。
     * @param redisKeys
     * @return
     */
    public Long del(RedisKey...redisKeys){
        String[] keys=new String[redisKeys.length];
        for (int i = 0,j=redisKeys.length; i < j; i++) {
            keys[i]=redisKeys[i].getKey();
        }
        return jedis.del(keys);
    }

    /**
     * 该命令用于在 key 存在时删除 key。
     * @param redisKeys
     * @return
     */
    public Long del(String...redisKeys){
        return jedis.del(redisKeys);
    }

    /**
     * 序列化给定 key ，并返回被序列化的值。
     * @param redisKey
     * @return
     */
    public byte[] dump(RedisKey redisKey){
        return jedis.dump(redisKey.getKey());
    }

    /**
     * 序列化给定 key ，并返回被序列化的值。
     * @param redisKey
     * @return
     */
    public byte[] dump(String redisKey){
        return jedis.dump(redisKey);
    }

    /**
     * 检查给定 key 是否存在。
     * @param redisKey
     * @return
     */
    public boolean 	exists(RedisKey redisKey){
        return jedis.exists(redisKey.getKey());
    }

    /**
     * 检查给定 key 是否存在。
     * @param redisKey
     * @return
     */
    public boolean 	exists(String redisKey){
        return jedis.exists(redisKey);
    }

    /**
     * 为给定 key 设置过期时间，以秒计
     * @param redisKey
     * @param seconds
     */
    public void expire(RedisKey redisKey,int seconds){
        jedis.expire(redisKey.getKey(),seconds);
    }

    /**
     * 为给定 key 设置过期时间，以秒计
     * @param redisKey
     * @param seconds
     */
    public void expire(String redisKey,int seconds){
        jedis.expire(redisKey,seconds);
    }

    /**
     * 设置 key 的过期时间以毫秒计。
     * @param redisKey
     * @param seconds
     */
    public void pexpire(RedisKey redisKey,int seconds){
        jedis.pexpire(redisKey.getKey(),seconds);
    }

    /**
     * 设置 key 的过期时间以毫秒计。
     * @param redisKey
     * @param seconds
     */
    public void pexpire(String redisKey,int seconds){
        jedis.pexpire(redisKey,seconds);
    }

    /**
     * 为给定 key 设置过期时间，时间参数是 UNIX 时间戳。
     * @param redisKey
     * @param unixTime
     */
    public void expireAt(RedisKey redisKey,long unixTime){
        jedis.expireAt(redisKey.getKey(),unixTime);
    }

    /**
     * 为给定 key 设置过期时间，时间参数是 UNIX 时间戳。
     * @param redisKey
     * @param unixTime
     */
    public void expireAt(String redisKey,long unixTime){
        jedis.expireAt(redisKey,unixTime);
    }

    /**
     * 设置 key 过期时间的时间戳(unix timestamp) 以毫秒计
     * @param redisKey
     * @param millisecondsTimestamp
     */
    public void pexpireAt(RedisKey redisKey,long millisecondsTimestamp){
        jedis.pexpireAt(redisKey.getKey(),millisecondsTimestamp);
    }

    /**
     * 设置 key 过期时间的时间戳(unix timestamp) 以毫秒计
     * @param redisKey
     * @param millisecondsTimestamp
     */
    public void pexpireAt(String redisKey,long millisecondsTimestamp){
        jedis.pexpireAt(redisKey,millisecondsTimestamp);
    }

    /**
     * 查找所有符合给定模式( pattern)的 key 。
     * @param pattern 模式
     * @return
     */
    public Set<String> keys(String pattern){
        return jedis.keys(pattern);
    }

    /**
     * 得到所有的RedisKey
     * @return
     */
    public Set<String> keys(){
        return jedis.keys("*");
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中。
     * @param redisKey
     * @param dbNumber (0-15)
     * @return
     */
    public Long mover(RedisKey redisKey,int dbNumber){
        return jedis.move(redisKey.getKey(),dbNumber);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中。
     * @param redisKey
     * @param dbNumber (0-15)
     * @return
     */
    public Long mover(String redisKey,int dbNumber){
        return jedis.move(redisKey,dbNumber);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持。
     * @param redisKey
     * @return
     */
    public Long persist(RedisKey redisKey){
        return jedis.persist(redisKey.getKey());
    }

    /**
     * 移除 key 的过期时间，key 将持久保持。
     * @param redisKey
     * @return
     */
    public Long persist(String redisKey){
        return jedis.persist(redisKey);
    }

    /**
     *以毫秒为单位返回 key 的剩余的过期时间。
     * @param redisKey
     * @return
     */
   public Long pttl(RedisKey redisKey){
        return jedis.pttl(redisKey.getKey());
   }

    /**
     * 以毫秒为单位返回 key 的剩余的过期时间。
     * @param redisKey
     * @return
     */
    public Long pttl(String redisKey){
        return jedis.pttl(redisKey);
    }

    /**
     *以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     * @param redisKey
     * @return
     */
    public Long ttl(RedisKey redisKey){
        return jedis.ttl(redisKey.getKey());
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     * @param redisKey
     * @return
     */
    public Long ttl(String redisKey){
        return jedis.ttl(redisKey);
    }

    /**
     * 从当前数据库中随机返回一个 key 。
     * @return
     */
    public String randomkey(){
        return jedis.randomKey();
    }

    /**
     * 修改 key 的名称
     * @param redisKey
     * @param newKey 新的key
     * @return
     */
    public String rename(RedisKey redisKey,String newKey){
        return redisKey.rename(newKey);
    }

    /**
     * 修改 key 的名称
     * @param redisKey
     * @param newKey 新的key
     * @return
     */
    public String rename(String redisKey,String newKey){
        return jedis.rename(redisKey,newKey);
    }

    /**
     *仅当 newkey 不存在时，将 key 改名为 newkey 。
     * @param redisKey
     * @param newKey
     * @return
     */
    public Long renamenx(String redisKey,String newKey){
        return jedis.renamenx(redisKey,newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 key 改名为 newkey 。
     * @param redisKey
     * @param newKey
     * @return
     */
    public Long renamenx(RedisKey redisKey,String newKey){
        return redisKey.renamenx(newKey);
    }

    /**
     * 迭代数据库中的数据库键。
     * @param cursor
     * @param scanParams
     * @return
     */
    public ScanResult<String> scan(String cursor, ScanParams scanParams){
        return jedis.scan(cursor,scanParams);
    }

    /**
     * 返回 key 所储存的值的类型。
     * @param redisKey
     * @return
     */
    public String type(String redisKey){
        return jedis.type(redisKey);
    }

    /**
     * 删除所有的Key
     * @return
     */
    public String flushAll(){
        return jedis.flushAll();
    }

    /**
     * 删除当前DB内所有的key
     * @return
     */
    public String flushDB(){
        return jedis.flushDB();
    }

    /**
     * 关闭Redis连接
     */
    public void close(){
        jedis.close();
    }

    @Override
    public Iterator<String> iterator() {
        return scan(ScanParams.SCAN_POINTER_START,new ScanParams()).getResult().iterator();
    }
}
