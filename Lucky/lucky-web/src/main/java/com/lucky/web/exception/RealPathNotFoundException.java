package com.lucky.web.exception;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 18:01
 */
public class RealPathNotFoundException extends RuntimeException {

    public RealPathNotFoundException(Throwable e){
        super("您没有配置「docBase」，无法获取其中的文件！",e);
    }
}
