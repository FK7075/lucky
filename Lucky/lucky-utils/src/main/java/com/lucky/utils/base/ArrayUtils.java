package com.lucky.utils.base;


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
}
