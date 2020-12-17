package com.lucky.jacklamb.reverse;

/**
 * 封装着一个表对应JavaBean的所有源代码
 * @author fk-7075
 *
 */
public class GetJavaSrc {
	
	private String className;//类名
	private String pack;//所在包
	private String impor;//导入的包
	private String javaSrc;//源代码
	private String toString;//toString代码
	private String constructor;//无参构造器源码
	private String parameterConstructor;//包含所有属性的有参构造器


	
	public String getConstructor() {
		return constructor;
	}


	public void setConstructor(String constructor) {
		this.constructor = constructor;
	}


	public String getParameterConstructor() {
		return parameterConstructor;
	}


	public void setParameterConstructor(String parameterConstructor) {
		this.parameterConstructor = parameterConstructor;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getPack() {
		return pack;
	}


	public void setPack(String pack) {
		this.pack = pack;
	}


	public String getImpor() {
		return impor;
	}


	public void setImpor(String impor) {
		this.impor = impor;
	}


	public String getJavaSrc() {
		return javaSrc;
	}


	public void setJavaSrc(String javaSrc) {
		this.javaSrc = javaSrc;
	}


	public String getToString() {
		return toString;
	}


	public void setToString(String toString) {
		this.toString = toString;
	}
	

	@Override
	public String toString() {
		return pack+"\n"+impor+"\n"+javaSrc+toString;
	}


}
