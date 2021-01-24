package com.lucky.web.core.parameter.analysis;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.ParameterUtils;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * 基本类型解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 9:17
 */
public class BaseParameterAnalysis implements ParameterAnalysis{

    @Override
    public double priority() {
        return 6;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter,String asmParamName) {
        if(parameter.isAnnotationPresent(RequestBody.class)){
            return false;
        }
        Class<?> parameterType = parameter.getType();
        //Java基本类型以及基本类型的包装类型以及他们的数组类型
        if(ClassUtils.isPrimitive(parameterType)
                ||ClassUtils.isSimple(parameterType)
                || ClassUtils.isSimpleArray(parameterType)){
            return true;
        }
        if(Collection.class.isAssignableFrom(parameterType)){
            Class<?>[] genericTypes = ParameterUtils.getGenericType(parameter);
            if(genericTypes==null){
                return false;
            }
            Class<?> genericType = genericTypes[0];
            if(ClassUtils.isSimple(genericType)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter,Type genericParameterType, String asmParamName) {
        String paramName=getParamName(parameter,asmParamName);
        Class<?> parameterType = parameter.getType();

        //可以在Request参数中拿到
        if(model.parameterMapContainsKey(paramName)){
            if(parameterType.isArray()){
                return model.getParams(paramName,parameterType);
            }
            if(Collection.class.isAssignableFrom(parameterType)){
                return getCollectionByArray(parameter,model.getParams(paramName));
            }
            return JavaConversion.strToBasic(model.getParameter(paramName),parameterType);
        }else if(model.restMapContainsKey(paramName)){
            return JavaConversion.strToBasic(model.getRestParam(paramName),parameterType);
        }else{  //尝试解析@Param中配置的默认值

            String defaultValue = getParamDefaultValue(parameter);
            if(Assert.isNull(defaultValue)){
                return null;
            }
            //以ioc:开头的表示从IOC容器中取值
            if(defaultValue.startsWith("ioc:")){
                String iocId=defaultValue.substring(4);
                ApplicationContext appContext=AutoScanApplicationContext.create();
                if(appContext.isIOCId(iocId)){
                    return appContext.getBean(iocId);
                }
                return null;
            }
            if(parameterType.isArray()){
                return JavaConversion.strArrToBasicArr(defaultValue.split(","),parameterType);
            }
            if(Collection.class.isAssignableFrom(parameterType)){
                return getCollectionByArray(parameter,defaultValue.split(","));
            }
            return JavaConversion.strToBasic(defaultValue,parameterType);
        }
    }

    private Collection<?> getCollectionByArray(Parameter parameter,String[] parameterValue){
        Class<?> genericClass = ParameterUtils.getGenericType(parameter)[0];
        Class<?> parameterClass = parameter.getType();
        Object[] array = JavaConversion.strArrToBasicArr(parameterValue, genericClass);
        if(List.class.isAssignableFrom(parameterClass)){
            return ArrayUtils.arrayToList(array);
        }
        return ArrayUtils.arrayToSet(array);
    }

}
