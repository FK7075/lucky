package com.lucky.aop.core;

import com.lucky.framework.container.Injection;
import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.base.BaseUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.util.List;

public class AopChain {
	
	private List<AopPoint> points;
	
	private int index=-1;
	
	private Object target;
	
	private Object[] params;
	
	private MethodProxy methodProxy;
	
	public AopChain(List<AopPoint> points, Object target, Object[] params, MethodProxy methodProxy) {
		this.points = points;
		this.target = target;
		this.params = params;
		this.methodProxy = methodProxy;
	}

	int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public MethodProxy getMethodProxy() {
		return methodProxy;
	}

	public void setMethodProxy(MethodProxy methodProxy) {
		this.methodProxy = methodProxy;
	}

	public Object proceed() throws Throwable {
		Object result;
		if(++index==points.size()) {
			result=methodProxy.invokeSuper(target, params);
		}else {
			AopPoint point=points.get(index);
//			Injection.injection(new Module(BaseUtils.lowercaseFirstLetter(point.getClass().getName()),"aop",point));
			result=point.proceed(this);
		}
		return result;
	}
	
	

}
