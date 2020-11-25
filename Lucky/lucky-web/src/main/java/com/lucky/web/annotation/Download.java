  package com.lucky.web.annotation;

  import java.lang.annotation.*;

  /**
   * MVC中定义一个文件下载的操作，只能使用在Controller的方法映射方法上
   * @author fk-7075
   *
   */
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Download {

      /**
       * 接受URL请求中包含的文件相对docBase文件夹的相对路径的参数值<br>
       * eg:http://localhost:8080/download?file="image/1.jpg"<br>
       * -@Download(name="file")
       * @return
       */
      String name() default "";

      /**
       * 要下载文件的绝对路径
       * @return
       */
      String path() default "";

      /**
       * 要下载文件相对docBase文件夹的相对路径
       * @return
       */
      String docPath() default "";

      /**
       * 暴露一个提供给外界下载的文件库
       * 默认：docBase文件夹
       * 没有前缀：使用docBase的相对路径
       * abs:开头表示使用绝对路径
       * http开头表示暴露一个网络上的文件库
       * @return
       */
      String library() default "";

      /**
       * 下载网络上的资源（eg:https://github.com/FK7075/lucky-ex/blob/noxml/image/images.png）
       * @return
       */
      String url() default "";
  }
