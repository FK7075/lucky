package com.lucky.redis.core.pojo;

import com.lucky.redis.core.RedisKey;
import com.lucky.utils.annotation.NonNull;
import com.lucky.utils.reflect.ClassUtils;
import redis.clients.jedis.ListPosition;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/10 下午12:39
 */
public abstract class RList<T> extends RedisKey<String,T> implements Iterable<T>{

    public RList(){
        super();
    }

    public RList(String key) {
        super(key);
    }

    public RList(int dbNubmer, String key) {
        super(dbNubmer, key);
    }

    @Override
    public void initGeneric() {
        keyType=String.class;
        valueType= ClassUtils.getGenericType(this.getClass().getGenericSuperclass())[0];
    }

    @Override
    public void setKey(String rKey) {
        this.key = "RList<"+valueType.getTypeName()+">-["+rKey+"]";
        this.key=this.key.replaceAll(" ","");
    }

    /**
     * 返回一个Iterator，用于遍历这个RList
     * @return
     */
    @NonNull
    public Iterator<T> iterator(){
        Iterator<T> iterator=new Iterator<T>() {
            int index=0;
            boolean isRead=false;
            boolean isRemove=false;
            @Override
            public boolean hasNext() {
                if(index<size()){
                    isRead=false;
                    isRemove=false;
                    return true;
                }
                return false;
            }

            @Override
            public T next() {
                long size=isRead?size()+1:size();
                if(index<size){
                    if(isRemove){
                        throw new UnsupportedOperationException("Element has been deleted");
                    }
                    if(!isRead){
                        T pojo= getByIndex(index);
                        index++;
                        isRead=true;
                        return pojo;
                    }else{
                        return getByIndex(index-1);
                    }
                }
                return null;
            }

            @Override
            public void remove() {
                long size=isRead?size()+1:size();
                if(index<size){
                    if(isRead){
                        if (isRemove){
                            throw new UnsupportedOperationException("Element has been deleted");
                        }
                        lremByIndex(index-1);
                        index=index-1;
                        isRemove=true;
                    }else{
                        if (isRemove){
                            throw new UnsupportedOperationException("Element has been deleted");
                        }
                        lremByIndex(index);
                        isRemove=true;
                    }
                }else{
                    throw new UnsupportedOperationException("remove");
                }
            }
        };
        return iterator;
    }

    /**
     * 对象集合转Json数组
     * @param pojoList 对象集合
     * @param isRev 是否逆序
     * @return
     */
    private String[] list2JsonArray(List<T> pojoList, boolean isRev){
        String[] jsonpojo=new String[pojoList.size()];
        if(isRev){
            for (int j=pojoList.size()-1,i =j; i >-1 ; i--) {
                jsonpojo[j-i]=valueSerialization(pojoList.get(i));
            }
        }else{
            for (int i = 0,j=pojoList.size(); i <j ; i++) {
                jsonpojo[i]=valueSerialization(pojoList.get(i));
            }
        }
        return jsonpojo;
    }

    /**
     * 将一个或多个对象插入到列表头部
     * @param pojo
     */
    public Long lpush(T...pojo){
        String[] jsonpojo=new String[pojo.length];
        for (int i = 0,j=pojo.length; i <j ; i++) {
            jsonpojo[i]=valueSerialization(pojo[i]);
        }
        return jedis.lpush(key,jsonpojo);
    }

    /**
     * 将一个对象集合插入到列表头部
     * @param pojoList
     */
    public Long lpushAll(List<T> pojoList){
        return jedis.lpush(key,list2JsonArray(pojoList,false));
    }

    /**
     * 将一个对象集合逆向插入到列表头部
     * @param pojoList
     */
    public Long lpushAllRev(List<T> pojoList){
        return jedis.lpush(key,list2JsonArray(pojoList,true));
    }

    /**
     * 将一个或多个对象插入到列表尾部
     * @param pojo
     */
    public Long rpush(T...pojo){
        String[] jsonpojo=new String[pojo.length];
        for (int i = 0,j=pojo.length; i <j ; i++) {
            jsonpojo[i]=valueSerialization(pojo[i]);
        }
        return jedis.rpush(key,jsonpojo);
    }

    /**
     * 将一个对象集合插入到列表尾部
     * @param pojoList
     */
    public Long rpushAll(List<T> pojoList){
        return jedis.rpush(key,list2JsonArray(pojoList,false));
    }

    /**
     * 将一个对象集合逆向插入到列表尾部
     * @param pojoList
     */
    public Long rpushAllRev(List<T> pojoList){
        return jedis.rpush(key,list2JsonArray(pojoList,true));
    }

    /**
     * 移除index位置的元素
     * @param index 要移除元素的索引
     */
    public Long lremByIndex(int index){
        if(index<0||index>size()-1){
            throw new ArrayIndexOutOfBoundsException("index="+index);
        }
        jedis.lset(key,index,DEL);
        return jedis.lrem(key,1,DEL);
    }

    /**
     * 移除列表中的pojo元素，如果有多个相同的pojo元素，会将所有的都移除
     * @param pojo 要移除的对象
     */
    public Long lremAll(T pojo){
        return jedis.lrem(key,0,valueSerialization(pojo));
    }

    /**
     * 移除列表中的pojo元素，如果有多个相同的pojo元素，会移除前count个
     * @param pojo 要移除的对象
     * @param count 存在多个相同元素时移除的个数
     */
    public Long lrem(T pojo,int count){
        return jedis.lrem(key,count,valueSerialization(pojo));
    }

    /**
     * 通过索引值得到一个元素
     * @param index 索引
     * @return
     */
    public T getByIndex(int index){
        return valueDeserialization(jedis.lindex(key,index));
    }

    /**
     * 得到列表中指定位置范围的所有元素
     * @param start 开始位置
     * @param stop 结束位置
     * @return
     */
    public List<T> getByLimit(int start,int stop){
        List<String> strTs = jedis.lrange(key, start, stop);
        return strTs.stream().map((sp)->{
            T pojo = valueDeserialization( sp);
            return pojo;
        }).collect(Collectors.toList());
    }

    /**
     * 得到列表中所有的元素
     * @return
     */
    public List<T> getAll(){
        return getByLimit(0,-1);
    }

    /**
     * 获取列表的长度
     * @return
     */
    public Long size(){
        return jedis.llen(key);
    }

    /**
     * 删除该RedisKey和列表中的所有元素
     */
    public Long del(){
        return jedis.del(key);
    }

    /**
     * 移出并获取列表的第一个元素
     * @return
     */
    public T lpop(){
        return valueDeserialization(jedis.lpop(key));
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @return
     */
    public T blpop(){
        List<String> blpop = jedis.blpop(key);
        if(blpop!=null&&!blpop.isEmpty()){
            return valueDeserialization(blpop.get(0));
        }
        return null;
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param timout 等待超时时间
     * @return
     */
    public T blpop(int timout){
        List<String> blpop = jedis.blpop(timout,key);
        if(blpop!=null&&!blpop.isEmpty()){
            return valueDeserialization(blpop.get(0));
        }
        return null;
    }

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     * @return
     */
    public T rpop(){
        return valueDeserialization(jedis.rpop(key));
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @return
     */
    public T brpop(){
        List<String> blpop = jedis.brpop(key);
        if(blpop!=null&&!blpop.isEmpty()){
            return valueDeserialization(blpop.get(0));
        }
        return null;
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param timout 超时时间
     * @return
     */
    public T brpop(int timout){
        List<String> blpop = jedis.brpop(timout,key);
        if(blpop!=null&&!blpop.isEmpty()){
            return valueDeserialization(blpop.get(0));
        }
        return null;
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     * @param start 开始位置
     * @param stop 结束位置
     */
    public String ltrim(int start,int stop){
        return jedis.ltrim(key, start, stop);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它;
     * 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param destinationT 待插入的列表
     * @param timout 等待超时时间
     * @return
     */
    public T brpoplpush(RList<T> destinationT,int timout){
        return valueDeserialization(jedis.brpoplpush(key,destinationT.getKey(),timout));
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     * @param destinationT 待插入的列表
     * @return
     */
    public T rpoplpush(RList<T> destinationT){
        return valueDeserialization(jedis.rpoplpush(key,destinationT.getKey()));
    }

    public Long linsert(ListPosition listPosition, T pivot, T value){
        return jedis.linsert(key,listPosition,valueSerialization(pivot),valueSerialization(value));
    }

    /**
     * 关闭Redis连接
     */
    public void close(){
        jedis.close();
    }
}
