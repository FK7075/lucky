package com.lucky.redis.serialization;

import com.lucky.framework.serializable.SerializationException;
import com.lucky.framework.serializable.SerializationScheme;
import com.lucky.framework.serializable.implement.JDKSerializationScheme;
import com.lucky.redis.core.StringSerializationScheme;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化方案
 * @author fk
 * @version 1.0
 * @date 2020/12/10 0010 16:35
 */
@SuppressWarnings("all")
public abstract class RedisSerialization<K,V> {

    /** 针对Key的序列化方案*/
    private SerializationScheme keySerializationScheme;
    /** 针对Value的序列化方案*/
    private SerializationScheme valueSerializationScheme;
    /** Key的类型*/
    protected Type keyType;
    /** Value的类型*/
    protected Type valueType;

    public RedisSerialization(){
        keySerializationScheme = new StringSerializationScheme();
        valueSerializationScheme =new JDKSerializationScheme();
    }

    /**
     * 设置Key的序列化方案
     * @param keySerializationScheme Key的序列化方案
     */
    public void setKeySerializationScheme(SerializationScheme keySerializationScheme) {
        this.keySerializationScheme = keySerializationScheme;
    }

    /**
     * 设置Value的序列化方案
     * @param valueSerializationScheme Value的序列化方案
     */
    public void setValueSerializationScheme(SerializationScheme valueSerializationScheme) {
        this.valueSerializationScheme = valueSerializationScheme;
    }

    /**
     * 将Key序列化
     * @param key KEY
     * @return
     * @throws IOException
     */
    public String keySerialization(K key){
        try {
            return keySerializationScheme.serialization(key);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * 将Value序列化
     * @param key VALUE
     * @return
     * @throws IOException
     */
    public String valueSerialization(V value){
        try {
            return valueSerializationScheme.serialization(value);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * 将Key反序列化
     * @param key KEY
     * @return
     * @throws IOException
     */
    public K keyDeserialization(String objectStr){
        try {
            return (K)keySerializationScheme.deserialization(keyType,objectStr);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    /**
     * 将Value反序列化
     * @param key VALUE
     * @return
     * @throws IOException
     */
    public V valueDeserialization(String objectStr){
        try {
            return (V)valueSerializationScheme.deserialization(valueType,objectStr);
        } catch (Exception e) {
           throw new SerializationException(e);
        }
    }
}
