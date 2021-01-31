package com.lucky.cloud.client.annotation;


import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:28
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerRequest {

}
