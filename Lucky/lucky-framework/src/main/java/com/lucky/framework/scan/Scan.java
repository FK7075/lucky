 package com.lucky.framework.scan;

 import com.lucky.framework.annotation.Component;
 import com.lucky.framework.uitls.reflect.AnnotationUtils;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;

 import java.io.IOException;
 import java.net.*;
 import java.util.*;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;

 /**
 * 包扫描的基类
 * @author fk-7075
 *
 */
public abstract class Scan {

	private static final Logger log= LogManager.getLogger(Scan.class);
	/** Component组件*/
	protected Set<Class<?>> componentClass;

	 public Set<Class<?>> getComponentClass() {
		 return componentClass;
	 }

	 public Scan(Class<?> applicationBootClass) {
		componentClass=new HashSet<>(225);
	}
	
	/**
	 * 自动扫描
	 */
	public abstract void autoScan();

	public void load(Class<?> beanClass){
		if(beanClass.isAnnotation()){
			return;
		}
		if(AnnotationUtils.strengthenIsExist(beanClass, Component.class)){
			componentClass.add(beanClass);
		}
	}
}
