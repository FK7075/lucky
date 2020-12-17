 package com.lucky.framework.scan;

 import com.lucky.framework.annotation.Component;
 import com.lucky.framework.spi.LuckyFactoryLoader;
 import com.lucky.utils.reflect.AnnotationUtils;
 import com.lucky.utils.reflect.ClassUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.stream.Collectors;

 /**
 * 包扫描的基类
 * @author fk-7075
 *
 */
public abstract class Scan {

	 private static final Logger log= LoggerFactory.getLogger(Scan.class);
	protected static ClassLoader loader;
	/** Component组件*/
	protected Set<Class<?>> componentClass;

	 public Set<Class<?>> getComponentClass() {
		 return componentClass.stream()
				 .filter(c-> AnnotationUtils.strengthenIsExist(c,Component.class))
				 .collect(Collectors.toSet());
	 }

	 public Set<Class<?>> getAllClasses(){
	 	return componentClass;
	 }

	 public Scan(Class<?> applicationBootClass) {
	 	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	 	loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
		componentClass=new HashSet<>(225);
		List<String> spareComponents = LuckyFactoryLoader.loadFactoryNames(SpareComponents.class, loader);
		 for (String spareComponent : spareComponents) {
			 Class<?> aClass = ClassUtils.forName(spareComponent, loader);
			 if(!AnnotationUtils.strengthenIsExist(aClass,Component.class)){
				 componentClass.add(aClass);
			 }
		 }
	}
	
	/**
	 * 自动扫描
	 */
	public abstract void autoScan();

	public void load(Class<?> beanClass){
		if(beanClass.isAnnotation()){
			return;
		}
		componentClass.add(beanClass);
	}
}
