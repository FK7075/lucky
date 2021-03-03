package com.lucky.jacklamb.exception;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/3 0003 9:44
 */
public class RuntimeSqlException extends RuntimeException {

    private static final long serialVersionUID = 5224696788505678598L;

    public RuntimeSqlException() {
        super();
    }

    public RuntimeSqlException(String message) {
        super(message);
    }

    public RuntimeSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeSqlException(Throwable cause) {
        super(cause);
    }

}
