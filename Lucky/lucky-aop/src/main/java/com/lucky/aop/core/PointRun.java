package com.lucky.aop.core;

import com.lucky.aop.annotation.*;
import com.lucky.aop.aspectj.AopChainProceedingJoinPoint;
import com.lucky.aop.aspectj.constant.AspectJ;
import com.lucky.aop.conf.AopConfig;
import com.lucky.aop.enums.Location;
import com.lucky.aop.exception.AopParamsConfigurationException;
import com.lucky.aop.utils.PointRunUtils;
import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Injection;
import com.lucky.framework.container.Module;
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 转换器，用于将AOP注解标注的Method转化为AopPoint
 */
public class PointRun {

	public static final Class<? extends Annotation>[] EXPAND_ANNOTATIONS=
			new Class[]{After.class,AfterReturning.class,
					AfterThrowing.class,Around.class,Before.class};

	/** 环绕增强的执行节点*/
	private AopPoint point;
	/** 增强的方法*/
	public Method method;

	private final AopExecutionChecker aopExecutionChecker= AopConfig.defaultAopConfig().getAopExecutionChecker();

	public Method getMethod() {
		return method;
	}

	/**
	 * 使用一个Point对象构造PointRun
	 * @param point
	 */
	public PointRun(AopPoint point) {
		Method proceedMethod= MethodUtils.getDeclaredMethod(point.getClass(),"proceed",AopChain.class);
		Around exp = proceedMethod.getAnnotation(Around.class);
		this.point = point;
		this.point.setPriority(exp.priority());
		this.aopExecutionChecker.setAspectMethod(proceedMethod);
		this.aopExecutionChecker.setPositionExpression(exp.expression());
	}
	
	/**
	 * 使用Point类型对象的Class来构造PointRun
	 * @param pointClass
	 */
	public PointRun(Class<? extends AopPoint> pointClass) {
		Method proceedMethod=MethodUtils.getDeclaredMethod(pointClass,"proceed", AopChain.class);
		Around exp = proceedMethod.getAnnotation(Around.class);
		this.point = ClassUtils.newObject(pointClass);
		this.point.setPriority(exp.priority());
		this.aopExecutionChecker.setAspectMethod(proceedMethod);
		this.aopExecutionChecker.setPositionExpression(exp.expression());
	}

	/**
	 * 使用增强类的实例对象+增强方法Method来构造PointRun
	 * @param aspectObject 增强类实例
	 * @param aspectMethod 增强(方法)
	 */
	public PointRun(Object aspectObject, Method aspectMethod) {
		this.method=aspectMethod;
		Class<?> aspectClass = aspectObject.getClass();
		Annotation annotation=AnnotationUtils.getByArray(aspectMethod, (Class<? extends Annotation>[]) ArrayUtils.merge(EXPAND_ANNOTATIONS,AspectJ.ASPECTJ_EXPANDS_ANNOTATION));
		Location location = PointRunUtils.getLocation(annotation);
		this.point=new MethodAopPoint(aspectObject,location,aspectMethod);
		this.point.setPriority(PointRunUtils.getPriority(aspectClass, aspectMethod));
		this.aopExecutionChecker.setAspectMethod(aspectMethod);
		this.aopExecutionChecker.setPositionExpression(PointRunUtils.getPointcutExecution(aspectClass,aspectMethod,annotation));
	}

	public AopPoint getPoint() {
		return point;
	}

	public void setPoint(AopPoint point) {
		this.point = point;
	}
	
	/**
	 * 检验当前方法是否符合该Point的执行标准
	 * @param targetClass 真实类的CLass
	 * @param method
	 * @return
	 */
	public boolean methodExamine(Class<?> targetClass,Method method) {
		return aopExecutionChecker.methodExamine(targetClass,method);
	}

	public boolean classExamine(Module module){
		return aopExecutionChecker.classExamine(module);
	}

	public static class MethodAopPoint extends AopPoint{
		private final Object aspectObject;
		private final Location location;
		private final Method aspectMethod;
		private boolean isFirst;

		public MethodAopPoint(Object aspectObject, Location location, Method aspectMethod) {
			this.aspectObject = aspectObject;
			this.location = location;
			this.aspectMethod = aspectMethod;
			isFirst=true;
			aroundMethodParamCheck();
		}

		private void aroundMethodParamCheck(){
			if(location==Location.AROUND){
				Parameter[] parameters = aspectMethod.getParameters();
				int aopChain=0;
				int joinPoint=0;
				for (Parameter parameter : parameters) {
					Class<?> parameterType = parameter.getType();
					if (AopChain.class.isAssignableFrom(parameterType)) {
						aopChain++;
					}
					if(ProceedingJoinPoint.class.isAssignableFrom(parameterType)){
						joinPoint++;
					}
				}
				//没有AopChain参数也没有ProceedingJoinPoint参数
				if(aopChain==0 && joinPoint==0){
					throw new AopParamsConfigurationException(
							String.format("环绕增强方法参数中必须带有一个`%s`类型或者`%s`类型的参数. [aopChain=%s,proceedingJoinPoint=%s]\n\t错误位置: %s",
									      AopChain.class.getName(),
									      ProceedingJoinPoint.class.getName(),
									      aopChain,
									      joinPoint,
									      aspectMethod)
					);
				}

				//AopChain参数和ProceedingJoinPoint参数同时存在
				if(aopChain!=0 && joinPoint!=0){
					throw new AopParamsConfigurationException(
							String.format("环绕增强方法参数中只能有一个`%s`类型或者`%s`类型的参数. [aopChain=%s,proceedingJoinPoint=%s]\n\t错误位置: %s",
									AopChain.class.getName(),
									ProceedingJoinPoint.class.getName(),
									aopChain,
									joinPoint,
									aspectMethod)
					);
				}

				//没有AopChain参数，但是存在多个ProceedingJoinPoint参数
				if(aopChain==0 && joinPoint>1){
					throw new AopParamsConfigurationException(
							String.format("环绕增强方法参数中只能存在一个`%s`类型的参数. [aopChain=%s,proceedingJoinPoint=%s]\n\t错误位置: %s",
									ProceedingJoinPoint.class.getName(),
									aopChain,
									joinPoint,
									aspectMethod)
					);
				}

				//没有ProceedingJoinPoint参数，但是存在多个AopChain参数
				if(joinPoint==0 && aopChain>1){
					throw new AopParamsConfigurationException(
							String.format("环绕增强方法参数中只能存在一个`%s`类型的参数. [aopChain=%s,proceedingJoinPoint=%s]\n\t错误位置: %s",
									AopChain.class.getName(),
									aopChain,
									joinPoint,
									aspectMethod)
					);
				}
			}
		}

		@Override
		public Object proceed(AopChain chain) throws Throwable {
			if(isFirst){
				Injection.injection(aspectObject,"aspect");
				isFirst=false;
			}
			if(location==Location.BEFORE) {
				perform(aspectObject, aspectMethod,chain,null,null,-1);
				return chain.proceed();
			}else if(location==Location.AFTER) {
				long start = System.currentTimeMillis();
				Object result=null;
				try {
					result=chain.proceed();
					return result;
				}catch (Throwable e){
					throw e;
				}finally {
					long end = System.currentTimeMillis();
					perform(aspectObject, aspectMethod,chain,null,result,end-start);
				}
			}else if(location==Location.AROUND){
				return perform(aspectObject, aspectMethod,chain,null,null,-1);
			}else if(location==Location.AFTER_RETURNING){
				Object result=null;
				try {
					long start = System.currentTimeMillis();
					result=chain.proceed();
					long end = System.currentTimeMillis();
					perform(aspectObject, aspectMethod,chain,null,result,end-start);
					return result;
				}catch (Throwable e){
					throw e;
				}
			}else if(location==Location.AFTER_THROWING){
				long start = System.currentTimeMillis();
				Object result=null;
				try {
					result=chain.proceed();
					return result;
				}catch (Throwable e){
					long end = System.currentTimeMillis();
					perform(aspectObject, aspectMethod,chain,e,null,end-start);
				}

			}
			return null;
		}

		//执行增强方法
		private Object perform(Object expand, Method expandMethod,AopChain chain,Throwable e,Object r,long t) {
			return MethodUtils.invoke(expand,expandMethod,setParams(expandMethod,chain,e,r,t));
		}

		//设置增强方法的执行参数@Param配置
		private Object[] setParams(Method expandMethod,AopChain chain,Throwable ex,Object result,long runtime) {
			int index;
			String aopParamValue,indexStr;
			Parameter[] parameters = expandMethod.getParameters();
			Object[] expandParams=new Object[parameters.length];
			TargetMethodSignature targetMethodSignature = tlTargetMethodSignature.get();
			AopChainProceedingJoinPoint joinPoint=new AopChainProceedingJoinPoint(chain,targetMethodSignature);
			ApplicationContext applicationContext= AutoScanApplicationContext.create();
			for(int i=0;i<parameters.length;i++) {
				Class<?> paramClass = parameters[i].getType();
				if(parameters[i].isAnnotationPresent(Param.class)){
					aopParamValue=parameters[i].getAnnotation(Param.class).value();
					if(aopParamValue.startsWith("ref:")) {//取IOC容器中的值
						if("ref:".equals(aopParamValue.trim())) {
							expandParams[i]=applicationContext.getBean(parameters[i].getType());
						} else {
							expandParams[i]=applicationContext.getBean(aopParamValue.substring(4));
						}
					}else if(aopParamValue.startsWith("ind:")) {//目标方法中的参数列表值中指定位置的参数值
						indexStr=aopParamValue.substring(4).trim();
						try {
							index=Integer.parseInt(indexStr);
						}catch(NumberFormatException e) {
							throw new AopParamsConfigurationException("错误的表达式，参数表达式中的索引不合法，索引只能为整数！错误位置："+expandMethod+"@Param("+aopParamValue+")=>err");
						}
						if(!targetMethodSignature.containsIndex(index)) {
							throw new AopParamsConfigurationException("错误的表达式，参数表达式中的索引超出参数列表索引范围！错误位置："+expandMethod+"@Param("+aopParamValue+")=>err");
						}
						expandParams[i]=targetMethodSignature.getParamByIndex(index);
					}else {//根据参数名得到具体参数
						if("return".equals(aopParamValue)){
							expandParams[i]=result;
							continue;
						}
						if("runtime".equals(aopParamValue)&&paramClass==long.class){
							expandParams[i]=runtime;
							continue;
						}
						if(!targetMethodSignature.containsParamName(aopParamValue)) {
							expandParams[i]=null;
						}else{
							expandParams[i]=targetMethodSignature.getParamByName(aopParamValue);
						}
					}
				}else{
					if(JoinPoint.class.isAssignableFrom(paramClass)){
						expandParams[i]=joinPoint;
					}else if(TargetMethodSignature.class.isAssignableFrom(paramClass)) {
						expandParams[i]=targetMethodSignature;
					}else if(AopChain.class.isAssignableFrom(paramClass)){
						expandParams[i]=chain;
					}else if(Class.class.isAssignableFrom(paramClass)){
						expandParams[i]=targetMethodSignature.getTargetClass();
					}else if(Method.class.isAssignableFrom(paramClass)){
						expandParams[i]=targetMethodSignature.getCurrMethod();
					}else if(applicationContext.getBean(paramClass).size()==1){
						expandParams[i]=applicationContext.getBean(paramClass).get(0);
					}else if(Object[].class==paramClass){
						expandParams[i]=targetMethodSignature.getParams();
					}else if(Parameter[].class==paramClass){
						expandParams[i]=targetMethodSignature.getParameters();
					}else if(Map.class.isAssignableFrom(paramClass)){
						Class<?>[] genericType = ClassUtils.getGenericType(parameters[i].getParameterizedType());
						if(genericType[0]==Integer.class&&genericType[1]==Object.class){
							expandParams[i]=targetMethodSignature.getIndexMap();
						}
						if(genericType[0]==String.class&&genericType[1]==Object.class){
							expandParams[i]=targetMethodSignature.getNameMap();
						}
					}else if(Annotation.class.isAssignableFrom(paramClass)){
						Class<? extends Annotation> ann= (Class<? extends Annotation>) paramClass;
						if(targetMethodSignature.getCurrMethod().isAnnotationPresent(ann)){
							expandParams[i]=targetMethodSignature.getCurrMethod().getAnnotation(ann);
						}
					}else if(Throwable.class.isAssignableFrom(paramClass)){
						expandParams[i]=ex;
					}else if(AopChain.class==paramClass){
						continue;
					}else{
						expandParams[i]=null;
					}
				}
			}
			return expandParams;
		}
	}
}
