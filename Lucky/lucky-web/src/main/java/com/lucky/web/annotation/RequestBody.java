package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * 将客户端传入的Json数据转化为Object对象
 * @author fk-7075
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {


}
