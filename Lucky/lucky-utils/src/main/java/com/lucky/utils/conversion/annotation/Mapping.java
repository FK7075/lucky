package com.lucky.utils.conversion.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Mappings.class)
public @interface Mapping {

    String source();

    String  target();
}
