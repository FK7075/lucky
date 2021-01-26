package com.lucky.web.mapping;

import com.lucky.framework.container.Module;
import com.lucky.utils.io.utils.AntPathMatcher;
import com.lucky.web.annotation.*;
import com.lucky.web.enums.Rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 类的映射解析，将一个Controller分解为多个URL映射
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:51
 */
public interface MappingAnalysis {

    AntPathMatcher antPathMatcher=new AntPathMatcher();

    Class<? extends Annotation>[] MAPPING_ANNOTATIONS=
            new Class[]{RequestMapping.class, GetMapping.class, PostMapping.class,
                    PutMapping.class, DeleteMapping.class};

    Class<? extends Annotation>[] CONTROLLER_ANNOTATIONS=
            new Class[]{Controller.class,RestController.class};

    Class<? extends Annotation>[] RUN_ANNOTATIONS=
            new Class[]{InitRun.class,CloseRun.class};

    /**
     * 将一个Controller解析为多个URL映射
     * @param controller Controller对象
     * @return
     */
    UrlMappingCollection analysis(Module controller);


    /**
     * 将一个ControllerAdvice解析为多个异常处理器
     * @param controllerAdvice ControllerAdvice对象
     * @return
     */
    ExceptionMappingCollection exceptionAnalysis(Module controllerAdvice);

    /**
     * Mapping方法的返回值类型检查，Rest为NO，但Method的返回值类型不为String或void时，需要抛出异常
     * @param rest Rest枚举
     * @param mapping Mapping方法
     */
    default void returnTypeCheck(Rest rest, Method mapping){
        if(rest!=Rest.NO){
            return;
        }
        final Class<?> returnType = mapping.getReturnType();
        if(returnType==void.class||returnType==String.class){
            return;
        }
        throw new MappingMethodReturnTypeCheckException(mapping);
    }

}
