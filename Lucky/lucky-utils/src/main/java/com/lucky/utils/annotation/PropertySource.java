package com.lucky.utils.annotation;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/22 0022 15:01
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertySource {

        /*

        这是一个将classpath下配置文件的配置映射到一个具体配置类的简单实例
        [1]为配置文件classpath: conf/test.yml中的一段自定义配置
        [2]为读取这段配置的配置列类的写法

        1.classpath: conf/test.yml
        -------------------------------
        lucky:
            name: Jack
            age:  24
            str-list:
              - string-001
              - string-002

          -------------------------------



          2.LuckyConfigurationProperties
          -------------------------------
          @Component
          @PropertySource(value="{conf/test.yml}",prefix="lucky",humpConversion="-")
          public class LuckyConfigurationProperties{
                private String name; //Jack
                private Integer age; //24
                //由于配置了humpConversion="-"，在读取配置时会将`strList`转化为`str-list`
                List<String> strList //[string-001,string-002]
          }
          -------------------------------
     */

    /** 指定classpath下的一组.yaml/.yml文件*/
    String[] value();
    /** 配置文件配置项的固定前缀*/
    String prefix() default "";
    /** 启用驼峰转换，设置一个链接符*/
    String humpConversion() default "";
}
