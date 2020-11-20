package com.lucky.web.enums;

public enum Rest {
	
	
	/**
	 * 不做转换，执行转发或重定向
	 */
	NO,

	/**
	 * 返回TEXT格式数据
	 */
	TXT,
	
	/**
	 * 返回JSON格式数据
	 */
	JSON,
	
	/**
	 * 返回XML格式数据
	 */
	XML

//	/**
//	 * 返回JDK序列化后的数据
//	 */
//	JDK
	

}
