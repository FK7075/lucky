package com.lucky.aop.annotation;

import com.lucky.aop.beanfactory.LuckyAopNestedProxyBeanFactory;
import com.lucky.aop.beanfactory.LuckyAopProxyPostProcessing;
import com.lucky.framework.annotation.Exclusions;
import com.lucky.framework.annotation.Imports;

import java.lang.annotation.*;

/**
 * 启用支持嵌套代理的AOP代理器
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 18:27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Imports(LuckyAopNestedProxyBeanFactory.class)
@Exclusions(LuckyAopProxyPostProcessing.class)
public @interface EnableAopNestedProxy {
}
