package com.lucky.utils.exception;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 14:26
 */
public class ClasspathFileLoadException extends RuntimeException{

    public ClasspathFileLoadException(String filePath, Throwable e){
        super(String.format("加载\"classpath:%s\"时出现错误,文件不存在或者没有访问的权限！",filePath),e);
    }
}
