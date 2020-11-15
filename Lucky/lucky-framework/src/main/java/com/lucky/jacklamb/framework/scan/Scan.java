 package com.lucky.jacklamb.framework.scan;

 import com.lucky.jacklamb.framework.annotation.Component;
 import com.lucky.jacklamb.framework.uitls.reflect.AnnotationUtils;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;

 import java.util.*;

 /**
 * 包扫描的基类
 * @author fk-7075
 *
 */
public abstract class Scan {

	private static final Logger log= LogManager.getLogger(Scan.class);
	/** Component组件*/
	protected Set<Class<?>> componentClass;

	public Scan(Class<?> applicationBootClass) {
		componentClass=new HashSet<>(225);
	}
	
	/**
	 * 自动扫描
	 */
	public abstract void autoScan();

	public void load(Class<?> beanClass){
		if(AnnotationUtils.strengthenIsExist(beanClass, Component.class)){
			componentClass.add(beanClass);
		}
	}

}
