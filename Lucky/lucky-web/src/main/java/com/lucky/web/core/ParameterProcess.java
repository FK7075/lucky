package com.lucky.web.core;

import com.lucky.framework.dm5.MD5Utils;
import com.lucky.framework.proxy.ASMUtil;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import com.lucky.framework.uitls.regula.Regular;
import com.lucky.web.annotation.Check;
import com.lucky.web.annotation.MD5;
import com.lucky.web.exception.ControllerParameterCheckException;
import com.lucky.web.mapping.UrlMapping;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 使用自定义的规则对Controller方法的执行参数进行二次加工
 * [加密、校验等]
 * @author fk
 * @version 1.0
 * @date 2020/11/25 0025 11:43
 */
public class ParameterProcess {

    /**
     * 执行参数进行二次加工
     * @param urlMapping 当前请求的映射
     */
    public void processAll(UrlMapping urlMapping){
        Parameter[] parameters= urlMapping.getParameters();
        Object[] runParams= urlMapping.getRunParams();
        String[] methodParamNames = ASMUtil.getMethodParamNames(urlMapping.getMapping());
        for (int i = 0,j=parameters.length; i < j; i++) {
            runParams[i]=process(parameters[i],runParams[i], ParameterUtils.getParamName(parameters[i],methodParamNames[i]));
        }
    }

    /**
     * MD5加密、参数格式校验[此方法可以被继承，在子类中可以自行扩展完整自定义的增强]
     * @param parameter 方法参数类型
     * @param runParam 参数值
     * @param paramName 参数名
     * @return
     */
    protected Object process(Parameter parameter,Object runParam,String paramName){
        check(parameter, runParam,paramName);
        return md5(parameter, runParam,paramName);
    }

    /**
     * MD5加密
     * @param parameter 方法参数类型
     * @param runParam 参数值
     * @param paramName 参数名
     * @return
     */
    protected Object md5(Parameter parameter,Object runParam,String paramName){
        if(AnnotationUtils.isExist(parameter, MD5.class)){
            MD5 md5 = parameter.getAnnotation(MD5.class);
            return MD5Utils.md5(runParam.toString(),md5.salt(),md5.cycle(),md5.capital());
        }
        return runParam;
    }

    /**
     * 参数格式校验
     * @param parameter 方法参数类型
     * @param runParam 参数值
     * @param paramName 参数名
     */
    protected void check(Parameter parameter,Object runParam,String paramName){
        if(Assert.isNull(runParam)){
            throw new ControllerParameterCheckException("Controller参数校验异常: 当前参数为NULL! ["+paramName+"=null] 校验位置："+parameter);
        }
        if(AnnotationUtils.isExist(parameter, Check.class)){
            Check check=AnnotationUtils.get(parameter,Check.class);
            String[] value = check.value();
            if(!Regular.check(runParam.toString(), value)){
                String error = check.error();
                if(Assert.isBlankString(error)){
                    throw new ControllerParameterCheckException("Controller参数校验异常: 当前参数为["+paramName+"=`"+runParam+"`] , 该参数值不符合正则约束："+ Arrays.toString(value));
                }
                throw new ControllerParameterCheckException("Controller参数校验异常: 当前参数为["+paramName+"=`"+runParam+"`] , 该参数值不符合[`"+error+"`]的编写规范！");
            }
        }
    }

}
