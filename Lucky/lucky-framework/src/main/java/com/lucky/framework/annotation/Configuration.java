package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * 定义一个Component组件的注册中心
 * @author fk-7075
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "configuration")
public @interface Configuration {

	String value() default "";

	double priority() default 5;
}