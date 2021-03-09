package com.lucky.aop.core;

public interface AopChain extends Cloneable{

	Object proceed() throws Throwable;

}
