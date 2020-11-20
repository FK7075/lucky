package com.lucky.web.annotation;

import com.lucky.web.enums.Rest;

import java.lang.annotation.*;

/**
 * 
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
	Rest value() default Rest.JSON;
}
