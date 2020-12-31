 package com.lucky.framework.scan;

 import com.lucky.framework.annotation.Component;
 import com.lucky.framework.annotation.LuckyBootApplication;
 import com.lucky.framework.exception.AddJarExpandException;
 import com.lucky.framework.spi.LuckyFactoryLoader;
 import com.lucky.utils.base.Assert;
 import com.lucky.utils.reflect.AnnotationUtils;
 import com.lucky.utils.reflect.ClassUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.stream.Collectors;
 import java.util.stream.Stream;

 /**
 * 包扫描的基类
 * @author fk-7075
 *
 */
public abstract class Scan {

	private static final Logger log= LoggerFactory.getLogger(Scan.class);
	/** 用于加载组件的类加载器*/
	protected static ClassLoader loader;
	/** 被排除组件的类型*/
	protected Set<Class<?>> exclusions;
	/** Component组件*/
	protected Set<Class<?>> componentClass;

	 public Set<Class<?>> getComponentClass() {
		 return componentClass.stream()
				 .filter(c-> AnnotationUtils.strengthenIsExist(c,Component.class))
				 .collect(Collectors.toSet());
	 }

	 public boolean exclusions(Class<?> beanClass){
	 	return exclusions.contains(beanClass);
	 }

	 public Set<Class<?>> getAllClasses(){
	 	return componentClass;
	 }

	 public Scan(Class<?> applicationBootClass) {
	 	boolean isA=initExclusions(applicationBootClass);
	 	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	 	loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
		componentClass=new HashSet<>(225);

		//从lucky.factories文件中加载组件
		List<String> spareComponents = LuckyFactoryLoader.loadFactoryNames(SpareComponents.class, loader);
		 for (String spareComponent : spareComponents) {
			 Class<?> aClass = ClassUtils.forName(spareComponent, loader);
			 if(!AnnotationUtils.strengthenIsExist(aClass,Component.class)){
			 	if(!exclusions.contains(aClass)){
					componentClass.add(aClass);
				}
			 }
		 }
		 if(isA) jarExpand(applicationBootClass);
	}

	private boolean initExclusions(Class<?> applicationBootClass){
		exclusions=new HashSet<>();
	 	//applicationBootClass为null，或者applicationBootClass为null没有被@LuckyBootApplication注解标注
	 	if(applicationBootClass==null||!AnnotationUtils.strengthenIsExist(applicationBootClass, LuckyBootApplication.class)){
	 		return false;
		}
		Stream.of(AnnotationUtils.strengthenGet(applicationBootClass, LuckyBootApplication.class).get(0).exclusions())
				.forEach(exclusions::add);
	 	if(!Assert.isEmptyCollection(exclusions)){
			log.info("Exclusions Classes `{}`",exclusions);
		}
	 	return true;
	}

	private void jarExpand(Class<?> applicationBootClass) {
		String jarExpand
				= AnnotationUtils.strengthenGet(applicationBootClass, LuckyBootApplication.class).get(0).jarExpand();
		if(!Assert.isBlankString(jarExpand)){
			List<JarExpand> jars = JarExpand.getJarExpandByJson(jarExpand);
			URL[] urls=new URL[1];
			for (JarExpand jar : jars) {
				jar.printJarInfo();
				try {
					urls[0]=new URL(jar.getJarPath());
				}catch (MalformedURLException e){
					throw new AddJarExpandException(jar.getJarPath());
				}

				LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls,loader);
				Set<Class<?>> beanClass = luckyURLClassLoader.getComponentClass(jar.getGroupId()).getBeanClass()
						.stream().filter(c->!exclusions.contains(c)).collect(Collectors.toSet());
				componentClass.addAll(beanClass);
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
		if(exclusions.contains(beanClass)){
			return;
		}
		componentClass.add(beanClass);
	}
}
