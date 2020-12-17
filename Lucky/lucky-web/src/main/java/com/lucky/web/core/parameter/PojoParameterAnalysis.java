package com.lucky.web.core.parameter;

import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;
import com.lucky.web.webfile.MultipartFile;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 包装类型解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 9:18
 */
public class PojoParameterAnalysis implements ParameterAnalysis{

    @Override
    public double priority() {
        return 6;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        if(parameter.isAnnotationPresent(RequestBody.class)){
            return false;
        }
        return !ClassUtils.isBasic(parameter.getType());
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, Type genericParameterType, String asmParamName) {
        Class<?> parameterClass = parameter.getType();
        Object result= ClassUtils.newObject(parameterClass);
        createObject(model,result);
        Field[] allFields = ClassUtils.getAllFields(parameterClass);
        for (Field fi : allFields) {
            String fieldName = fi.getName();
            Class<?> fieldClass = fi.getType();
            // pojo中含有@Upload返回的文件名
            if (model.uploadFileMapContainsKey(fieldName)) {
                File[] uploadFiles = model.getUploadFileArray(fieldName);
                if (fieldClass == String[].class) {
                    String[] uploadFileNames = new String[uploadFiles.length];
                    for (int x = 0; x < uploadFiles.length; x++) {
                        uploadFileNames[x] = uploadFiles[x].getName();
                    }
                    FieldUtils.setValue(result,fi,uploadFiles);
                } else if (fieldClass == String.class) {
                    FieldUtils.setValue(result,fi,uploadFiles[uploadFiles.length-1].getName());
                } else if (fieldClass == File.class) {
                    FieldUtils.setValue(result,fi,uploadFiles[uploadFiles.length-1]);
                } else if (fieldClass == File[].class) {
                    FieldUtils.setValue(result,fi,uploadFiles);
                }
            }else if(model.multipartFileMapContainsKey(fieldName)){
                MultipartFile[] multipartFileArray = model.getMultipartFileArray(fieldName);
                if (fieldClass ==  MultipartFile[].class) {
                    FieldUtils.setValue(result,fi,multipartFileArray);
                }else if(fieldClass ==MultipartFile.class){
                    FieldUtils.setValue(result,fi,multipartFileArray[multipartFileArray.length-1]);
                }
            }else if(model.parameterMapContainsKey(fieldName)){
                if(fieldClass.isArray()){
                    FieldUtils.setValue(result,fi,model.getParams(fieldName,fieldClass));
                }
                FieldUtils.setValue(result,fi,JavaConversion.strToBasic(model.getParameter(fieldName),fieldClass));
            }else if(model.restMapContainsKey(fieldName)){
                FieldUtils.setValue(result,fi, JavaConversion.strToBasic(model.getRestParam(fieldName),fieldClass));
            }

        }
        return result;

    }

    /**
     * 为Controller方法中的pojo属性注入request域或RestMap中对应的值
     * @param model
     * @param pojo
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object createObject(Model model, Object pojo){
        Field[] fields = ClassUtils.getAllFields(pojo.getClass());
        for (Field field : fields) {
            Class<?> fieClass = field.getType();
            if (ClassUtils.isBasic(fieClass)) {
                if (fieClass.isArray()) {
                    FieldUtils.setValue(pojo,field,model.getParams(field.getName(), fieClass));
                } else {
                    if (model.parameterMapContainsKey(field.getName())) {
                        FieldUtils.setValue(pojo,field, JavaConversion.strToBasic(model.getParameter(field.getName()), fieClass));
                    }
                    if (model.getRestMap().containsKey(field.getName())) {
                        FieldUtils.setValue(pojo,field, model.getRestParam(field.getName(), fieClass));
                    }
                }
            } else {
                Object object=ClassUtils.newObject(fieClass);
                object = createObject(model, object);
                FieldUtils.setValue(pojo,field,object);
            }
        }
        return pojo;
    }
}
