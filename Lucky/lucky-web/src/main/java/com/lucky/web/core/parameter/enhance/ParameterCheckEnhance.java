package com.lucky.web.core.parameter.enhance;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.regula.Regular;
import com.lucky.web.annotation.Check;
import com.lucky.web.exception.ControllerParameterCheckException;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 上午12:59
 */
public class ParameterCheckEnhance implements ParameterEnhance{
    @Override
    public double priority() {
        return 1;
    }

    @Override
    public Object enhance(Parameter parameter, Type genericType, Object runParam, String paramName) {
        if(AnnotationUtils.isExist(parameter, Check.class)){
            Assert.notNull(runParam,"Controller参数校验异常: 当前参数为NULL! ["+paramName+"=null] 校验位置："+parameter);
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
        return runParam;
    }
}
