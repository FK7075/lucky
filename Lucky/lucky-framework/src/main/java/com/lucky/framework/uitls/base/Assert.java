package com.lucky.framework.uitls.base;

import java.util.Collection;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/10/29 16:37
 */
public abstract class Assert {

    /**
     * 判断对象是否为空对象
     * @param obj 待判断的对象
     * @return
     */
    public static boolean isNull(Object obj){
        return obj==null;
    }

    /**
     * 判断对象是否为非空对象
     * @param obj 待判断的对象
     * @return
     */
    public static boolean isNotNull(Object obj){
        return !isNull(obj);
    }

    /**
     * 判断集合是否为空集合
     * @param collection 待判断的集合
     * @return
     */
    public static boolean isEmptyCollection(Collection<?> collection){
        return collection==null||collection.isEmpty();
    }

    /**
     * 判断字符串是否为空字符串
     * @param str 待判断的字符串
     * @return
     */
    public static boolean isBlankString(String str){
        return str==null||"".equals(str);
    }

    /**
     * 判断数组是否为空
     * @param objects 待判断的数组
     * @return
     */
    public static boolean isEmptyArray(Object[] objects){
        return objects==null||objects.length==0;
    }

    /**
     * 判断Map是否为空
     * @param map 待判断的Map
     * @return
     */
    public static boolean isEmptyMap(Map map){
        return map==null||map.isEmpty();
    }

    public static boolean strEndsWith(String str,String[] suffix){
        for (String s : suffix) {
            if(str.endsWith(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean strStartsWith(String str,String[] prefix){
        for (String s : prefix) {
            if(str.startsWith(s)){
                return true;
            }
        }
        return false;
    }
}
