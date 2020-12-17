package com.lucky.web.core.parameter.enhance;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 上午12:48
 */
public interface ParameterEnhance {

    double priority();

    /**
     * 参数增强，可以利用该接口对参数进行校验和增强操作
     * @param parameter 当前要处理的参数类型
     * @param genericType 当前要处理的参数的泛型类型
     * @param runParam 当前要处理的参数值
     * @param paramName 当前要处理的参数名
     * @return
     */
    Object enhance(Parameter parameter, Type genericType,Object runParam, String paramName);
}
