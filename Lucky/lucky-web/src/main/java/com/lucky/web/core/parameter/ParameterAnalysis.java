package com.lucky.web.core.parameter;

import com.lucky.framework.annotation.Param;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import com.lucky.web.core.Model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 参数解析的基类模板
 * can方法定义使用场景
 * analysis方法返回该场景下的解析数据
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 9:12
 */
public interface ParameterAnalysis {

    double priority();


    /**
     * 是否可以调用本类的解析逻辑
     * @param model 当前请求的Model对象
     * @param method 处理当前请求的Controller方法
     * @param parameter 当前要处理的方法参数
     * @param asmParamName ASM工具解析出的当前参数的参数名
     * @return
     */
    boolean can(Model model, Method method, Parameter parameter,String asmParamName);

    /**
     * 执行解析，并返回解析结果
     * @param model 当前请求的Model对象
     * @param method 处理当前请求的Controller方法
     * @param parameter 当前要处理的方法参数
     * @param asmParamName ASM工具解析出的当前参数的参数名
     * @return
     */
    Object analysis(Model model, Method method,Parameter parameter,String asmParamName) throws Exception;

    /**
     * 得到参数的参数名
     * @param parameter 当前要处理的方法参数
     * @param asmParamName ASM工具解析出的当前参数的参数名
     * @return
     */
    default String getParamName(Parameter parameter,String asmParamName){
        return ParameterUtils.getParamName(parameter,asmParamName);
    }

    /**
     * 得到Param注解中def的值
     * @param param
     * @return
     */
    default String getParamDefaultValue(Parameter param) {
        if (param.isAnnotationPresent(Param.class)) {
            Param rp = param.getAnnotation(Param.class);
            String defValue = rp.def();
            if ("null".equals(defValue)) {
                return null;
            }
            return defValue;
        } else {
            return null;
        }
    }
}
