package com.lucky.mybatis.annotation;

import com.lucky.framework.annotation.Exclusions;
import com.lucky.framework.annotation.Imports;
import com.lucky.mybatis.beanfactory.MybatisBeanFactory;
import com.lucky.mybatis.beanfactory.MybatisXmlBeanFactory;
import com.lucky.mybatis.beanfactory.MybatisPlusBeanFactory;
import com.lucky.mybatis.beanfactory.MybatisPlusXmlBeanFactory;

import java.lang.annotation.*;

/**
 * 使用MyBatis-Xml启动Mybatis
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/2 上午12:34
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Imports(MybatisXmlBeanFactory.class)
@Exclusions({MybatisBeanFactory.class, MybatisPlusBeanFactory.class, MybatisPlusXmlBeanFactory.class})
public @interface MyBatisXmlBoot {
}