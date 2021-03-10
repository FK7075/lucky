package com.lucky.mybatis.annotation;

import com.lucky.framework.annotation.Exclusions;
import com.lucky.framework.annotation.Imports;
import com.lucky.mybatis.beanfactory.MybatisBeanFactory;
import com.lucky.mybatis.beanfactory.*;

import java.lang.annotation.*;

/**
 * 使用MyBatis-Plus 和mybatis.xml启动Mybatis
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/2 上午12:34
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Imports(MybatisPlusXmlBeanFactory.class)
@Exclusions({MybatisBeanFactory.class, MybatisPlusBeanFactory.class, MybatisXmlBeanFactory.class})
public @interface EnableMyBatisPlusXmlLauncher {
}
