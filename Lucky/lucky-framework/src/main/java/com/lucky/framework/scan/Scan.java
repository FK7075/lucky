 package com.lucky.framework.scan;

 import com.lucky.framework.annotation.Component;
 import com.lucky.utils.reflect.AnnotationUtils;

 import java.lang.annotation.Annotation;
 import java.lang.reflect.Modifier;
 import java.util.*;
 import java.util.stream.Collectors;

 /**
 * 包扫描的基类
 * @author fk-7075
 *
 */
public abstract class Scan {

	/** 用于加载组件的类加载器*/
	protected static ClassLoader loader;
	/** 外部导入的组件以及需要排除的组件*/
	protected ImportAndExclusionUtils.ImportAndExclusion ie;

	 /**
	  * 获取所有组件类的Class
	  * @return
	  */
	public Set<Class<?>> getComponentClass() {
		 return getAllClasses().stream()
				 .filter(c-> AnnotationUtils.strengthenIsExist(c,Component.class))
				 .collect(Collectors.toSet());
	}

	 /**
	  * 检查当前类是否在排除列表中
	  * @param beanClass 带检查的类
	  * @return
	  */
	 public boolean exclusions(Class<?> beanClass){
		 for (Class<?> exclusion : ie.getExclusionClasses()) {
			 //是注解
			 if(Annotation.class.isAssignableFrom(exclusion)){
				 if(AnnotationUtils.isExist(beanClass, (Class<? extends Annotation>) exclusion)){
					 return true;
				 }
			 }

			 int modifiers = exclusion.getModifiers();
			 //是抽象类或者接口
			 if(Modifier.isInterface(modifiers)|| Modifier.isAbstract(modifiers)){
				 if(exclusion.isAssignableFrom(beanClass)){
					 return true;
				 }
			 }

			 //是一个具体的类
			 if(exclusion.equals(beanClass)){
				 return true;
			 }

		 }
		 return false;
	 }

	 /**
	  * 获取所有扫描得到的类的Class
	  * @return
	  */
	 public Set<Class<?>> getAllClasses(){
	 	return ie.subtraction();
	 }

	 public Scan(Class<?> applicationBootClass) {
		 ClassLoader cl = Thread.currentThread().getContextClassLoader();
		 loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
		 ImportAndExclusionUtils ieu=new ImportAndExclusionUtils(loader,applicationBootClass);
		 ie=ieu.getIe();
		 ie.log();
	}

	/**
	 * 自动扫描
	 */
	public abstract void autoScan();

	public void load(Class<?> beanClass){
		if(beanClass.isAnnotation()){
			return;
		}
		if(exclusions(beanClass)){
			return;
		}
		ie.addImportClass(beanClass);
	}
}
