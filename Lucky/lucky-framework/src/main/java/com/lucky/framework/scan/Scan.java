 package com.lucky.framework.scan;

 import com.lucky.framework.annotation.Component;
 import com.lucky.framework.annotation.JarScan;
 import com.lucky.framework.annotation.JarScans;
 import com.lucky.framework.annotation.LuckyBootApplication;
 import com.lucky.framework.exception.AddJarExpandException;
 import com.lucky.framework.spi.LuckyFactoryLoader;
 import com.lucky.utils.base.Assert;
 import com.lucky.utils.reflect.AnnotationUtils;
 import com.lucky.utils.reflect.ClassUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.lang.annotation.Annotation;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.ArrayList;
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
	 	//获取需要排除的类型
	 	initExclusions(applicationBootClass);
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
		 //加载Jar包中Bean的Class信息
		 loadJarExpand(applicationBootClass);
	}

	//获取需要排除的类型
	private void initExclusions(Class<?> applicationBootClass){
		exclusions=new HashSet<>();
	 	//applicationBootClass为null，或者applicationBootClass为null没有被@LuckyBootApplication注解标注
	 	if(applicationBootClass==null || !AnnotationUtils.strengthenIsExist(applicationBootClass, LuckyBootApplication.class)){
	 		return ;
		}
		Stream.of(AnnotationUtils.strengthenGet(applicationBootClass, LuckyBootApplication.class).get(0).exclusions())
				.forEach(exclusions::add);
	 	if(!Assert.isEmptyCollection(exclusions)){
			log.info("Exclusions Classes `{}`",exclusions);
		}
	}

	private void loadJarExpand(Class<?> applicationBootClass) {
		final List<JarExpand> jars = getJarExpandByBootClass(applicationBootClass);
		URL[] urls=new URL[1];
		for (JarExpand jar : jars) {
			log.info("Load external Jar groupId= `{}` , jarPath= `{}`",jar.getGroupId(),jar.getJarPath());
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

	private List<JarExpand> getJarExpandByBootClass(Class<?> bootClass){
		List<JarExpand> jars=new ArrayList<>();
		//applicationBootClass为null，或者applicationBootClass没有被@LuckyBootApplication而且也没有被@JarScan注解标注
		if(bootClass==null ||
				(!AnnotationUtils.strengthenIsExist(bootClass, LuckyBootApplication.class) && !bootClass.isAnnotationPresent(JarScans.class))){
			return jars;
		}
		if(AnnotationUtils.strengthenIsExist(bootClass, LuckyBootApplication.class)){
			final String jarExpand = AnnotationUtils.strengthenGet(bootClass, LuckyBootApplication.class).get(0).jarExpand();
			if(!Assert.isBlankString(jarExpand)){
				jars.addAll(JarExpand.getJarExpandByJsonFile(jarExpand));
			}
		}
		if(bootClass.isAnnotationPresent(JarScans.class)){
			JarScan[] jarScans = bootClass.getAnnotation(JarScans.class).value();
			for (JarScan jarScan : jarScans) {
				loadJaScan(jars,jarScan);
			}
		}else if(bootClass.isAnnotationPresent(JarScan.class)){
			loadJaScan(jars,bootClass.getAnnotation(JarScan.class));
		}
		return jars;
	}

	private void loadJaScan(List<JarExpand> jarExpands,JarScan jarScan){
		String jsonFile = jarScan.jsonFile();
		if(!Assert.isBlankString(jsonFile)){
			jarExpands.addAll(JarExpand.getJarExpandByJsonFile(jsonFile));
		}
		String jarPath = jarScan.jarPath();
		if(!Assert.isBlankString(jarPath)){
			jarExpands.add(new JarExpand(null,jarScan.groupId(),jarPath));
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
		if(isExclusion(beanClass)){
			return;
		}
		componentClass.add(beanClass);
	}

	private boolean isExclusion(Class<?> beanClass){
		for (Class<?> exclusion : exclusions) {
			if(Annotation.class.isAssignableFrom(exclusion)){
				if(AnnotationUtils.isExist(beanClass, (Class<? extends Annotation>) exclusion)){
					return true;
				}
			}else{
				if(exclusion.isAssignableFrom(beanClass)){
					return true;
				}
			}
		}
		return false;
	}
}
