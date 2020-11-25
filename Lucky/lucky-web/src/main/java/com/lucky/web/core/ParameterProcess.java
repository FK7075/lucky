package com.lucky.web.core;

import com.lucky.framework.proxy.ASMUtil;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import com.lucky.framework.uitls.regula.Regular;
import com.lucky.web.annotation.Check;
import com.lucky.web.exception.ControllerParameterCheckException;
import com.lucky.web.mapping.Mapping;

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
     * @param mapping 当前请求的yings
     */
    public void processAll(Mapping mapping){
        Parameter[] parameters=mapping.getParameters();
        Object[] runParams=mapping.getRunParams();
        String[] methodParamNames = ASMUtil.getMethodParamNames(mapping.getMapping());
        for (int i = 0,j=parameters.length; i < j; i++) {
            runParams[i]=process(parameters[i],runParams[i], ParameterUtils.getParamName(parameters[i],methodParamNames[i]));
        }
    }

    protected Object process(Parameter parameter,Object runParam,String paramName){
        check(parameter, runParam,paramName);
        return md5(parameter, runParam,paramName);
    }

    protected Object md5(Parameter parameter,Object runParamm,String paramName){
        return null;
    }

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
                throw new ControllerParameterCheckException("Controller参数校验异常: 当前参数为["+paramName+"=`"+runParam+"`]");
            }
        }
    }

}
