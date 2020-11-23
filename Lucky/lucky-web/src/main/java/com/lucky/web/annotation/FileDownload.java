package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @time 2020-5-28
 * 标注Api方法为一个文件下载的代理
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileDownload {
}
