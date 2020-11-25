package com.lucky.web.exception;

import com.lucky.web.mapping.UrlMapping;

import java.util.Arrays;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 14:50
 */
public class RepeatUrlMappingException extends RuntimeException{

    public RepeatUrlMappingException(UrlMapping urlMapping){
        String.format("URL映射重复定义异常！[%s]/%s 已经被定义!错误位置：Controller:[%s] Method:[%s]"
                , urlMapping.getUrl()
                ,Arrays.toString(urlMapping.getMethods())
                , urlMapping.getObject().getClass()
                , urlMapping.getMapping().getName());
    }
}
