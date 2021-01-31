package com.lucky.web.enums;

import com.lucky.web.httpclient.IContentType;

public enum Rest implements IContentType {

	/**
	 * 不做转换，执行转发或重定向
	 */
	NO(null),

	/**
	 * 返回TEXT格式数据
	 */
	TXT("text/plain"),
	
	/**
	 * 返回JSON格式数据
	 */
	JSON("application/json"),
	
	/**
	 * 返回XML格式数据
	 */
	XML("application/xml");

//	/**
//	 * 返回JDK序列化后的数据
//	 */
//	JDK

	Rest(String contentType){
		this.contentType=contentType;
	}

	private String contentType;

	@Override
	public String getContentType() {
		return contentType;
	}
}
