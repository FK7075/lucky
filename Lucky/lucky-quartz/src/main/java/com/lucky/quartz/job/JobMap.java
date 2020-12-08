package com.lucky.quartz.job;

import com.lucky.quartz.TargetJobRun;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/10 8:44
 */
public class JobMap {

    private static Map<String, TargetJobRun> jmap;

    static {
        jmap=new HashMap<>();
    }

    public static boolean has(String key){
        return jmap.containsKey(key);
    }

    public static void add(String key,TargetJobRun value){
        jmap.put(key, value);
    }

    public static void del(String key){
        jmap.remove(key);
    }

    public static TargetJobRun get(String key){
       return jmap.get(key);
    }
}
