package com.lucky.aop.enums;

public enum Location {
	
	/**
	 * 前置增强，只有当扩展方法返回true时执行目标方法
	 */
	CONDITIONS,
	/**
	 * 前置增强，并且无条件执行目标方法
	 */
	BEFORE,
	/**
	 * 后置增强
	 */
	AFTER,

	/**
	 * 环绕增强
	 */
	AROUND,

	/**
	 * 正常执行时执行
	 */
	AFTER_RETURNING,

	/**
	 * 出现异常时执行
	 */
	AFTER_THROWING
}
