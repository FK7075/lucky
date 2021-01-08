package com.lucky.framework.annotation;

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
     * 扫描外部Jar包得到组件
     * jarExpand的值是一个指向classpath路径下的一个json格式的扩展文件
     * 文件描述的是jar扩展的相关信息，例如：
     * [
     *  {
     *     "groupId"    : "org.jack.lamb",
     *     "jarPath"    : "jar:file:/E:/TEST/Lucky-v2/service-01/target/service-01-1.0-SNAPSHOT-jar-with-dependencies.jar!/"
     *  },
     *  ....
     *  {
     *     "groupId"    : "com.czx",
     *     "jarPath"    : "jar:file:/D:/jar/report-1.0-SNAPSHOT-jar-with-dependencies.jar!/"
     *  }
     * ]
     * @return
     */
    String jarExpand() default "";

    /**
     * 用于排除某个类型的组件扫描
     * @return
     */
    Class[] exclusions() default {};

    Class<?>[] beforeInit() default {};


}
