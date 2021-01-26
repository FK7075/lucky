package com.lucky.web.exception;

import com.lucky.web.mapping.UrlMapping;

import java.util.Arrays;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 14:50
 */
public class RepeatUrlMappingException extends RuntimeException{

    public RepeatUrlMappingException(UrlMapping u1,UrlMapping u2){
        super(String.format("URL映射语义重复异常！\n#\n### `%s%s` `%s%s` 存在语义重复问题!\n### 映射定义位置如下：\n### 1.%s#%s\n### 2.%s#%s\n#"
                , Arrays.toString(u1.getMethods())
                , u1.getUrl()
                , Arrays.toString(u2.getMethods())
                , u2.getUrl()
                , u1.getMapping().getDeclaringClass()
                , u1.getMapping().getName()
                , u2.getMapping().getDeclaringClass()
                , u2.getMapping().getName()
        ));
    }
}
