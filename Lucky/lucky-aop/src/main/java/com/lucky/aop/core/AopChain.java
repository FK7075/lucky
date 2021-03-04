package com.lucky.aop.core;

@FunctionalInterface
public interface AopChain {

	Object proceed() throws Throwable;
}
