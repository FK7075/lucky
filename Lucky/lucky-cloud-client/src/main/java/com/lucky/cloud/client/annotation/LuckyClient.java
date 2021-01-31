package com.lucky.cloud.client.annotation;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Plugin
@Component(type = "luckyHttpClient")
public @interface LuckyClient {
    String value();
    String id() default "";
    String registry() default "defaultZone";
}
