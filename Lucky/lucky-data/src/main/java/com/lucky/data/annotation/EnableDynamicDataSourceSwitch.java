package com.lucky.data.annotation;

import com.lucky.data.aspect.DynamicDataSourcePoint;
import com.lucky.framework.annotation.Imports;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/8 0008 10:39
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Plugin
@Imports(DynamicDataSourcePoint.class)
public @interface EnableDynamicDataSourceSwitch {
}
