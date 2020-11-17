package com.lucky.web.exception;

import com.lucky.web.mapping.Mapping;

import java.util.Arrays;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 14:50
 */
public class RepeatUrlMappingException extends RuntimeException{

    public RepeatUrlMappingException(Mapping mapping){
        String.format("URL映射重复定义异常！[%s]/%s 已经被定义!错误位置：Controller:[%s] Method:[%s]"
                ,mapping.getUrl()
                ,Arrays.toString(mapping.getMethods())
                ,mapping.getController().getClass()
                ,mapping.getMapping().getName());
    }
}
