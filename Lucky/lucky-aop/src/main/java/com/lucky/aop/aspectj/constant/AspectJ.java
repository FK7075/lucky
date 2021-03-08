package com.lucky.aop.aspectj.constant;

import org.aspectj.lang.annotation.*;

import java.lang.annotation.Annotation;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/7 下午10:35
 */
public class AspectJ {

    public static final Class<? extends Annotation>[] ASPECTJ_EXPANDS_ANNOTATION=new Class[]{
            After.class, Before.class, AfterReturning.class,
            AfterThrowing.class, Around.class
    };
}
