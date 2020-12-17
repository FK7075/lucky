package com.lucky.jacklamb.exception;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 9:51
 */
public class NotFoundDataSourceException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public NotFoundDataSourceException() {
        super("没有可用数据源，无法创建mapper接口的代理对象....");
    }

    public NotFoundDataSourceException(String message) {
        super(message);
    }

    public NotFoundDataSourceException(Throwable e) {
        super(e);
    }


    public NotFoundDataSourceException(String message,Throwable e) {
        super(message,e);
    }
}
