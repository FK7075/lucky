package com.lucky.utils.annotation;

import java.lang.annotation.*;

/**
 * 定义一个获取默认配置文件的配置的配置类
 * @author fk
 * @version 1.0
 * @date 2021/1/22 0022 14:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationProperties {

    /*

        这是一个将默认配置文件的配置映射到一个具体配置类的简单实例
        [1]为默认配置文件中的一段自定义配置
        [2]为读取这段配置的配置列类的写法

        1.application.yml
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
          @ConfigurationProperties(prefix="lucky",humpConversion="-")
          public class LuckyConfigurationProperties{
                private String name; //Jack
                private Integer age; //24
                //由于配置了humpConversion="-"，在读取配置时会将`strList`转化为`str-list`
                List<String> strList //[string-001,string-002]
          }
          -------------------------------
     */

    /** 配置文件配置项的固定前缀*/
    String prefix() default "";
    /** 启用驼峰转换，设置一个链接符*/
    String humpConversion() default "";
}
