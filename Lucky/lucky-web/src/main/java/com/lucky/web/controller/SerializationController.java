package com.lucky.web.controller;

import com.google.gson.reflect.TypeToken;
import com.lucky.framework.serializable.implement.xml.LXML;
import com.lucky.utils.serializable.json.LSON;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * 序列化操作的Controller基类「JSON」「XML」
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/3 上午2:02
 */
public abstract class SerializationController {

    protected static LSON lson=new LSON();

    /**
     * 将对象转化为JSON字符串
     * @param pojo
     * @return
     */
    protected String toJson(Object pojo){
        return lson.toJson(pojo);
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param targetClass
     * @param jsonString
     * @param <T>
     * @return
     */
    protected <T> T fromJson(Class<T> targetClass,String jsonString){
        return lson.fromJson(targetClass,jsonString);
    }

    protected <T> T fromJson(Class<T> pojoClass, Reader reader){
        return lson.fromJson(pojoClass, reader);
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param token
     * @param jsonString
     * @return
     */
    protected Object fromJson(TypeToken token, String jsonString){
        return lson.fromJson(token,jsonString);
    }

    protected Object fromJson(TypeToken token, Reader reader){
        return lson.fromJson(token.getType(),reader);
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param type
     * @param jsonString
     * @return
     */
    protected Object fromJson(Type type, String jsonString){
        return lson.fromJson(type,jsonString);
    }

    protected Object fromJson(Type type, Reader reader){
        return lson.fromJson(type,reader);
    }

    protected String toXml(Object pojo){
        return new LXML().toXml(pojo);
    }

    protected void toXml(Object pojo, Writer writer){
        new LXML().toXml(pojo,writer);
    }

    protected void toXml(Object pojo, OutputStream out){
        new LXML().toXml(pojo,out);
    }

    protected Object fromXml(String xmlStr){
        return new LXML().fromXml(xmlStr);
    }

    protected Object fromXml(Reader xmlReader){
        return new LXML().fromXml(xmlReader);
    }

    protected Object fromXml(InputStream xmlIn){
        return new LXML().fromXml(xmlIn);
    }
}
