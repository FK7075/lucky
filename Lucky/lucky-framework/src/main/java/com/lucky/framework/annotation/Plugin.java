package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * 插件
 * @author fk7075
 * @version 1.0
 * @date 2020/11/18 8:56
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Plugin {
}
