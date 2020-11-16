package com.lucky.framework.serializable.implement.xml;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/14 11:15
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XStreamAllowType {
    String value() default "XStream-XFL-FK@0922@0721";
}
