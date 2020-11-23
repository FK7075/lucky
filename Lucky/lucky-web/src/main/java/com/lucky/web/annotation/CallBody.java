package com.lucky.web.annotation;

import com.lucky.web.enums.Rest;

import java.lang.annotation.*;

/**
 * 调用远程接口，并将返回结果封装到参数中
 * @author fk-7075
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallBody {
	Rest value() default Rest.TXT;
}
