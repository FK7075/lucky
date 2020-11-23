package com.lucky.web.annotation;

import com.lucky.web.enums.Rest;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/23 下午11:40
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface RestController {

    /**
     * 指定一个URL映射
     * @return
     */
    String value() default "/";

    /**
     * 为该Controller组件指定一个唯一ID，默认会使用[首字母小写类名]作为组件的唯一ID
     * @return
     */
    String id() default "";

    /**
     *
     * @return
     */
    String callapi() default "";

    /**
     * 指定一些合法访问的ip地址，来自其他ip地址的请求将会被拒绝
     * @return
     */
    String[] ip() default {};

    /**
     * 指定一些合法访问的ip段，来自其他ip地址的请求将会被拒绝
     * @return
     */
    String[] ipSection() default {};

    /**
     * 指定对Controller中所有方法的返回值处理策略<br>
     * 1.Rest.NO(默认选项)：转发与重定向处理,只对返回值类型为String的结果进行处理<br>
     *  &nbsp;&nbsp;&nbsp;
     * a.转发到页面：无前缀 return page<br>
     * 	&nbsp;&nbsp;&nbsp;
     * b.转发到Controller方法:return forward:method<br>
     *	&nbsp;&nbsp;&nbsp;
     * c.重定向到页面：return page:pageing<br>
     *	&nbsp;&nbsp;&nbsp;
     * d.重定向到Controller方法：return redirect:method<br>
     * 2.Rest.TXT：将返回值封装为txt格式，并返回给客户端<br>
     * 3.Rest.JSON：将返回值封装为json格式，并返回给客户端<br>
     * 4.Rest.XML：将返回值封装为xml格式，并返回给客户端
     * @return
     */
    Rest rest() default Rest.JSON;
}
