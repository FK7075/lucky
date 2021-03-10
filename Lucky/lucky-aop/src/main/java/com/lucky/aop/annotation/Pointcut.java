package com.lucky.aop.annotation;

import com.lucky.aop.core.DefaultAopExecutionChecker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个切入点
 * @author fk
 * @version 1.0
 * @date 2021/3/8 0008 16:50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pointcut {

    /**
     * Lucky中内置了两种表达式引擎<br/>
     *    1. AspectJ的表达式解析引擎（默认） {@link AspectJAopExecutionChecker}<br/>
     *    2. Lucky提供的表达式解析引擎 {@link DefaultAopExecutionChecker}<br/>
     *
     * AspectJ表达式写法请参照Aspect官网<br/>
     *    <a href="https://www.eclipse.org/aspectj/docs.php">AspectJ文档</a><br/>
     * Lucky表达式写法：<br/>
     *    P:{包检验表达式}<br/>
     *    C:{N[类名检验表达式],I[IOC_ID校验表达式],T[IOC_TYPE校验表达式],A[是否被注解]}<br/>
     *    M:{N[方法名校验表达式],A[是否被注解],AC[访问修饰符],O[要增强的继承自Object对象的方法]}<br/>
     *    例如：
     *    1.要增强com.lucky.demo.LuckyHelloWorld中的hello方法的写法：<br/>
     *      P:{com.lucky.demo}C:{N[LuckyHelloWorld]}M:{N[hello]}<br/>
     *    2.要增强IOC容器中ID为bean-1和bean-2中所有以query结尾的方法的写法：<br/>
     *      P:{*}C:{I[bean-1,bean-2]}M:{N[*query]}
     *
     * @return 描述切入点的表达式
     */
    String value();
}
