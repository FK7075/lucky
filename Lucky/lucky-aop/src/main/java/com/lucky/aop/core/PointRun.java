package com.lucky.aop.core;

import com.lucky.aop.annotation.*;
import com.lucky.aop.enums.Location;
import com.lucky.aop.exception.AopParamsConfigurationException;
import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 转换器，用于将AOP注解标注的Method转化为AopPoint
 */
public class PointRun {

	private static final Class<? extends Annotation>[] EXPAND_ANNOTATIONS=
			new Class[]{After.class,AfterReturning.class,
					AfterThrowing.class,Around.class,Before.class};

	/** 环绕增强的执行节点*/
	private AopPoint point;
//	/** 当前增强要作用的类需要符合的规则*/
//	private String pointCutClass;
//	/** 当前增强要作用的类方法需要符合的规则*/
//	private String pointCutMethod;
//	/** 当前增强要作用的类方法需要被某个注解标注*/
//	private Class<? extends Annotation>[] pointCutMethodAnn;
	/** 增强的方法*/
	public Method method;
	private AopExecutionChecker aopExecutionChecker;

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
		this.aopExecutionChecker=new AopExecutionChecker(proceedMethod,exp.expres());
//		this.pointCutClass = exp.pointCutClass();
//		this.pointCutMethod = exp.pointCutMethod();
//		this.pointCutMethodAnn =exp.pointCutMethodAnn();
	}
	
	/**
	 * 使用Point类型对象的Class来构造PointRun
	 * @param pointClass
	 */
	public PointRun(Class<?> pointClass) {
		Method proceedMethod=MethodUtils.getDeclaredMethod(pointClass,"proceed", AopChain.class);
		Around exp = proceedMethod.getAnnotation(Around.class);
		this.point = (AopPoint) ClassUtils.newObject(pointClass);
		this.point.setPriority(exp.priority());
		this.aopExecutionChecker=new AopExecutionChecker(proceedMethod,exp.expres());
//		this.pointCutClass = exp.pointCutClass();
//		this.pointCutMethod = exp.pointCutMethod();
//		this.pointCutMethodAnn =exp.pointCutMethodAnn();

	}

	/**
	 * 使用增强类的实例对象+增强方法Method来构造PointRun
	 * @param expand 增强类实例
	 * @param method 增强(方法)
	 */
	public PointRun(Object expand, Method method) {
		this.method=method;
		Annotation ean = AnnotationUtils.getByArray(method, EXPAND_ANNOTATIONS);
		Location location=AnnotationUtils.strengthenGet(method,Expand.class).get(0).value();
		this.point=conversion(expand,method,location);
		this.point.setPriority((Double) AnnotationUtils.getValue(ean,"priority"));
		this.aopExecutionChecker=new AopExecutionChecker(method, (String) AnnotationUtils.getValue(ean,"expres"));
//		if(method.isAnnotationPresent(Before.class)) {
//			Before before=method.getAnnotation(Before.class);
//			this.point=conversion(expand,method, Location.BEFORE);
//			this.point.setPriority(before.priority());
//			this.pointCutClass = before.pointCutClass();
//			this.pointCutMethod = before.pointCutMethod();
//			this.pointCutMethodAnn =before.pointCutMethodAnn();
//		}else if(method.isAnnotationPresent(After.class)) {
//			After after=method.getAnnotation(After.class);
//			this.point=conversion(expand,method,Location.AFTER);
//			this.point.setPriority(after.priority());
//			this.pointCutClass = after.pointCutClass();
//			this.pointCutMethod = after.pointCutMethod();
//			this.pointCutMethodAnn =after.pointCutMethodAnn();
//		}else if(method.isAnnotationPresent(Around.class)){
//			Around around=method.getAnnotation(Around.class);
//			this.point=conversion(expand,method,Location.AROUND);
//			this.point.setPriority(around.priority());
//			this.pointCutClass = around.pointCutClass();
//			this.pointCutMethod = around.pointCutMethod();
//			this.pointCutMethodAnn =around.pointCutMethodAnn();
//		}else if(method.isAnnotationPresent(AfterReturning.class)){
//			AfterReturning afterReturning=method.getAnnotation(AfterReturning.class);
//			this.point=conversion(expand,method,Location.AFTER_RETURNING);
//			this.point.setPriority(afterReturning.priority());
//			this.pointCutClass = afterReturning.pointCutClass();
//			this.pointCutMethod = afterReturning.pointCutMethod();
//			this.pointCutMethodAnn =afterReturning.pointCutMethodAnn();
//		}else if(method.isAnnotationPresent(AfterThrowing.class)){
//			AfterThrowing afterThrowing=method.getAnnotation(AfterThrowing.class);
//			this.point=conversion(expand,method,Location.AFTER_THROWING);
//			this.point.setPriority(afterThrowing.priority());
//			this.pointCutClass = afterThrowing.pointCutClass();
//			this.pointCutMethod = afterThrowing.pointCutMethod();
//			this.pointCutMethodAnn =afterThrowing.pointCutMethodAnn();
//		}
	}

//	public String getPointCutClass() {
//		return pointCutClass;
//	}
//
//	public void setPointCutClass(String mateClass) {
//		this.pointCutClass = mateClass;
//	}
//
//	public String getPointCutMethod() {
//		return pointCutMethod;
//	}
//
//	public void setPointCutMethod(String mateMethod) {
//		this.pointCutMethod = mateMethod;
//	}
//
//	public Class<? extends Annotation>[] getPointCutMethodAnn() {
//		return pointCutMethodAnn;
//	}
//
//	public void setPointCutMethodAnn(Class<? extends Annotation>[] pointCutMethodAnn) {
//		this.pointCutMethodAnn = pointCutMethodAnn;
//	}

	public AopPoint getPoint() {
		return point;
	}

	public void setPoint(AopPoint point) {
		this.point = point;
	}
	
	/**
	 * 检验当前方法是否符合该Point的执行标准
	 * @param method
	 * @return
	 */
	public boolean methodExamine(Method method) {
//		return standardStart(method);
		return aopExecutionChecker.methodExamine(method);
	}

	public boolean classExamine(Module module){
		return aopExecutionChecker.classExamine(module);
	}
	
//	/**
//	 * 遍历mateMethod，逐个验证
//	 * @param method 当前Method
//	 * @return
//	 */
//	private boolean standardStart(Method method) {
//		String methodName=method.getName();
//		Parameter[] parameters = method.getParameters();
//		String[] pointCutMethodArray=pointCutMethod.split(",");
//		//注解验证,如果存在注解配置，则pointCutMethod配置将失效
//		if(pointCutMethodAnn.length!=0){
//			return standardAnnotation(method);
//		}
//
//
//		for(String methodCut:pointCutMethodArray) {
//			methodCut=methodCut.trim();
//
//			//访问修饰符验证
//			if("public".equals(methodCut)){
//				//是否配置了public,如果配置了public，则所有非public都将不会执行该增强
//				if(!Modifier.isPublic(method.getModifiers())) {
//					return false;
//				}
//			}
//			if("private".equals(methodCut)){
//				//如果配置了private，则所有非private都将不会执行该增强
//				if(!Modifier.isPrivate(method.getModifiers())) {
//					return false;
//				}
//			}
//			if("protected".equals(methodCut)){
//				//如果配置了protected，则所有非protected都将不会执行该增强
//				if(!Modifier.isProtected(method.getModifiers())) {
//					return false;
//				}
//			}
//			//方法名验证以及方法名+参数类型简写验证
//			if("*".equals(methodCut)) {
//				return true;
//			}else if(methodCut.contains("(")&&methodCut.endsWith(")")){
//				if(standardMethod(methodName,parameters,methodCut)) {
//					return true;
//				}
//			}else {
//				if(standardName(methodName,methodCut)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 注解验证
//	 * @param method
//	 * @return
//	 */
//	private boolean standardAnnotation(Method method){
//		for (Class<? extends Annotation> aClass : pointCutMethodAnn) {
//			if(method.isAnnotationPresent(aClass)){
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 方法名验证
	 * @param mothodName 当前方法的方法名
	 * @param pointcut 配置中配置的标准方法名
	 * @return
	 */
	private boolean standardName(String mothodName,String pointcut) {
		if(pointcut.startsWith("!")) {
			return !(mothodName.equals(pointcut.substring(1)));
		}else if(pointcut.startsWith("*")) {
			return mothodName.endsWith(pointcut.substring(1));
		}else if(pointcut.endsWith("*")) {
			return mothodName.startsWith(pointcut.substring(0, pointcut.length()-1));
		}else {
			return mothodName.equals(pointcut);
		}
	}
	
	/**
	 * 方法名+方法参数验证
	 * @param mothodName 当前方法的方法名
	 * @param parameters 当前方法的参数列表
	 * @param pointcut 配置中配置的标准方法名+参数
	 * @return
	 */
	private boolean standardMethod(String mothodName,Parameter[] parameters,String pointcut) {
		int indexOf = pointcut.indexOf("(");
		String methodNameStr;
		boolean pass=true;
		String[] methodParamStr=pointcut.substring(indexOf+1, pointcut.length()-1).split(" ");
		if(pointcut.startsWith("!")) {
			if(methodParamStr.length!=parameters.length) {
				return true;
			}
			methodNameStr=pointcut.substring(1, indexOf);
			for(int i=0;i<methodParamStr.length;i++) {
				if(!(methodParamStr[i].equals(parameters[i].getType().getSimpleName()))) {
					pass=false;
					break;
				}
			}
			return !(standardName(mothodName,methodNameStr)&&pass);
		}else {//没有  ！
			if(methodParamStr.length!=parameters.length) {
				return false;
			}
			methodNameStr=pointcut.substring(0, indexOf);
			for(int i=0;i<methodParamStr.length;i++) {
				if(!(methodParamStr[i].equals(parameters[i].getType().getSimpleName()))) {
					pass=false;
					break;
				}
			}
			return standardName(mothodName,methodNameStr)&&pass;
		}
	}
	
	/**
	 * 使用增强类的执行参数构造Point
	 * @param expand 增强类实例
	 * @param expandMethod 增强类方法
	 * @param location 增强位置
	 * @return
	 */
	private AopPoint conversion(Object expand, Method expandMethod, final Location location) {


		if(location==Location.AROUND){
			Parameter[] parameters = expandMethod.getParameters();
			int cursor=0;
			for(int i=0;i<parameters.length;i++) {
				if(AopChain.class.isAssignableFrom(parameters[i].getType())){
					cursor++;
				}
			}
			if(cursor==0){
				throw new AopParamsConfigurationException("环绕增强方法中必须要带有一个`com.lucky.framework.core.AopChain`类型的参数，该方法中没有AopChain参数，错误位置："+method);
			}
			if(cursor>1){
				throw new AopParamsConfigurationException("环绕增强方法中有且只能有一个`com.lucky.framework.core.AopChain`类型的参数，该方法中包含"+cursor+"个AopChain参数，错误位置："+method);
			}

		}

		AopPoint cpoint=new AopPoint() {
			
			@Override
			public Object proceed(AopChain chain) throws Throwable {
				if(location==Location.BEFORE) {
					perform(expand,expandMethod,chain,null,null,-1);
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
						perform(expand,expandMethod,chain,null,result,end-start);
					}
				}else if(location==Location.AROUND){
					return perform(expand,expandMethod,chain,null,null,-1);
				}else if(location==Location.AFTER_RETURNING){
					Object result=null;
					try {
						long start = System.currentTimeMillis();
						result=chain.proceed();
						long end = System.currentTimeMillis();
						perform(expand,expandMethod,chain,null,result,end-start);
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
						perform(expand,expandMethod,chain,e,null,end-start);
					}

				}
				return null;
			}

			//执行增强方法
			private Object perform(Object expand, Method expandMethod,AopChain chain,Throwable e,Object r,long t) {
				return MethodUtils.invoke(expand,expandMethod,setParams(expandMethod,chain,e,r,t));
			}
			
			//设置增强方法的执行参数-@AopParam配置
			private Object[] setParams(Method expandMethod,AopChain chain,Throwable ex,Object result,long runtime) {
				int index;
				String aopParamValue,indexStr;
				Parameter[] parameters = expandMethod.getParameters();
				Object[] expandParams=new Object[parameters.length];
				TargetMethodSignature targetMethodSignature = tlTargetMethodSignature.get();
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
						if(TargetMethodSignature.class.isAssignableFrom(paramClass)) {
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
		};
		return cpoint;
	}
}
