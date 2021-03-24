package com.lucky.utils.base;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午9:18
 */
public abstract class ArrayUtils {

    public static  <T> Set<T> arrayToSet(T[] array){
        return Stream.of(array).collect(Collectors.toSet());
    }

    public static <T> List<T> arrayToList(T[] array){
        return Stream.of(array).collect(Collectors.toList());
    }

    public static <T> T[] listToArray(List<T> list){
        Assert.notNull(list,"list is null!");
        Object[] array=new Object[list.size()];
        list.toArray(array);
        return (T[]) array;
    }

    public static InputStream byteArrayToInputStream(byte[] byteData){
        return new ByteArrayInputStream(byteData);
    }

    public static  Object[] merge(Object[]... arrays){
        List<Object> list=new ArrayList<>();
        for (Object[] array : arrays) {
            list.addAll(Arrays.asList(array));
        }
        Object[] result=new Class[list.size()];
        list.toArray(result);
        return result;
    }

    public static boolean containStr(String[] strArray,String str){
        if(Assert.isEmptyArray(strArray)){
            return false;
        }
        for (String s : strArray) {
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }
}
