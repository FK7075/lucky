package com.lucky.aop.core;

import com.lucky.aop.proxy.LuckyAopInvocationHandler;
import com.lucky.aop.proxy.LuckyAopMethodInterceptor;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.proxy.JDKProxy;

import java.util.List;

public class ProxyClassFactory {
	
	private static ProxyClassFactory proxyFactory;
	
	private ProxyClassFactory() {}
	
	public static ProxyClassFactory createProxyFactory() {
		if(proxyFactory==null)
			proxyFactory=new ProxyClassFactory();
		return proxyFactory;
	}
	
	/**
	 * 得到一个代理对象
	 * @param target 真实类的对象
	 * @param pointRuns 增强Points(可变参形式)
	 * @return
	 */
	public Object getProxy(Object target, PointRun...pointRuns) {
		Class<?> targetClass = target.getClass();
		return getCglibProxy(targetClass,pointRuns);

	}
	
	/**
	 * 得到一个代理对象
	 * @param target 真实类的对象
	 * @param pointRuns 增强Points(集合参形式)
	 * @return
	 */
	public Object getProxy(Object target,List<PointRun> pointRuns) {
		Class<?> targetClass = target.getClass();
		return getCglibProxy(targetClass,pointRuns);
	}

	public Object getCglibProxy(Class<?> targetClass,List<PointRun> pointRuns){
		return CglibProxy.getCglibProxyObject(targetClass,new LuckyAopMethodInterceptor(pointRuns));
	}

	public Object getCglibProxy(Class<?> targetClass,PointRun...pointRuns){
		return CglibProxy.getCglibProxyObject(targetClass,new LuckyAopMethodInterceptor(pointRuns));
	}

}
