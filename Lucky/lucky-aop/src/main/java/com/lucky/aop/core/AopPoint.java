package com.lucky.aop.core;

/**
 * 环绕增强的执行节点,该抽象类的子类对象将会是一个环形增强的切面
 * @author fk-7075
 *
 */
public abstract class AopPoint implements Cloneable {

	/** 优先级*/
	private double priority=5;

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	/**
	 * 当前方法的签名信息b
	 */
	protected ThreadLocal<TargetMethodSignature> tlTargetMethodSignature;

	public void init(TargetMethodSignature targetMethodSignature) {
		tlTargetMethodSignature=new ThreadLocal<>();
		tlTargetMethodSignature.set(targetMethodSignature);
	}

	public AopPoint cloneObject(TargetMethodSignature targetMethodSignature){
		AopPoint clone = (AopPoint) clone();
		clone.init(targetMethodSignature);
		return clone;
	}

	
	
	/**
	 * 抽象方法，用于产生一个环绕增强方法
	 * @param chain
	 * @return
	 */
	public abstract Object proceed(AopChain chain) throws Throwable;


	public Object clone(){
		try {
			return super.clone();
		}catch (CloneNotSupportedException  e){
			throw new RuntimeException(e);
		}
	}

}
