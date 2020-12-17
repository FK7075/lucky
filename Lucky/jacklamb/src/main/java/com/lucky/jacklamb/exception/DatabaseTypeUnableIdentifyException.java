package com.lucky.jacklamb.exception;

/**
 * 数据库类型无法识别异常
 * @author fk-7075
 *
 */
public class DatabaseTypeUnableIdentifyException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DatabaseTypeUnableIdentifyException(String message) {
		super(message);
	}
	
	public DatabaseTypeUnableIdentifyException(String message, Throwable cause) {
		super(message,cause);
	}

}
