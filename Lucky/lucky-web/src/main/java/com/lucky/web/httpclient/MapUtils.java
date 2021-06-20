package com.lucky.web.httpclient;

import com.lucky.utils.base.Assert;

import java.util.Map;

/**
 * MAP工具类
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/11 下午11:15
 */
public abstract class MapUtils {

    public static Object get(Map<String,Object> source,String keys){
        return toGet(source,keys,keys);
    }

    private static Object toGet(Map<String,Object> source,String key,String completeKey) {
        Assert.notNull(source, "source is null");
        Assert.notNull(key, "key is null");
        if (!key.contains(":")) {
            return source.get(key);
        }
        int index = key.indexOf(":");
        String tempKey = key.substring(0,index);
        String leftKey = key.substring(index+1);
        Object tempObject = source.get(tempKey);
        if(tempObject instanceof Map){
            return toGet((Map<String,Object>)tempObject,leftKey,completeKey);
        }
        String type = tempObject == null ? "null" : tempObject.getClass().getName();
        throw new RuntimeException("在解析'"+completeKey+"'时出错，其中'"+completeKey.substring(0,completeKey.length()-leftKey.length())+
                "'部分对应值的类型为'"+type+"'，无法继续解析！");
    }
}
