package com.lucky.web.mapping;

import com.lucky.framework.container.Module;
import com.lucky.web.annotation.*;

import java.lang.annotation.Annotation;

/**
 * 类的映射解析，将一个Controller分解为多个URL映射
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:51
 */
public interface MappingAnalysis {

    public static final Class<? extends Annotation>[] MAPPING_ANNOTATIONS=
            new Class[]{RequestMapping.class, GetMapping.class, PostMapping.class,
                    PutMapping.class, DeleteMapping.class};

    public static final Class<? extends Annotation>[] CONTROLLER_ANNOTATIONS=
            new Class[]{Controller.class,RestController.class};

    /**
     * 将一个Controller解析为多个URL映射
     * @param controller Controller对象
     * @return
     */
    MappingCollection analysis(Module controller);


    /**
     * 将一个ControllerAdvice解析为多个异常处理器
     * @param controllerAdvice ControllerAdvice对象
     * @return
     */
    ExceptionMappingCollection exceptionAnalysis(Module controllerAdvice);

}
