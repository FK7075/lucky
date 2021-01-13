package com.lucky.framework.annotation;

import com.lucky.framework.scan.exclusions.Exclusions;
import com.lucky.framework.scan.imports.Imports;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/30 0030 15:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface LuckyBootApplication {

    /**
     * 扫描外部Jar包得到组件<br/>
     * jarExpand的值是一个json格式的扩展文件<br/>
     * 文件描述的是jar扩展的相关信息<br/>
     *      1.ClassPath路径写法   ：classpath:/jar-expand.json<br/>
     *      2.绝对路径写法        ：D:/jar/lucky/jar-expand.json<br/>
     * 文件内容如下：<br/>
     * [<br/>
     *  {<br/>
     *     "groupId"    : "org.jack.lamb",<br/>
     *     "jarPath"    : "jar:file:/E:/TEST/Lucky-v2/service-01/target/service-01-1.0-SNAPSHOT-jar-with-dependencies.jar!/"<br/>
     *  },<br/>
     *  ....<br/>
     *  {<br/>
     *     "groupId"    : "com.czx",<br/>
     *     "jarPath"    : "jar:file:/D:/jar/report-1.0-SNAPSHOT-jar-with-dependencies.jar!/"<br/>
     *  }<br/>
     * ]<br/>
     */
    String jarExpand() default "";

    /**
     * 用于指定组件扫描时排除某个类型的组件<br/>
     *    1.组件类的Class 如：UserMapper.class<br/>
     *    2.注解类型Class<? extends Annotation><br/>
     *    3.{@link Exclusions}接口实现类的子类的Class
     */
    Class[] exclusions() default {};

    /**
     * 配置的这些Class将会参与组件扫描<br/>
     *     1.组件类的Class 如：UserMapper.class<br/>
     *     3.{@link Imports}接口实现类的子类的Class
     */
    Class[] imports() default {};

}
