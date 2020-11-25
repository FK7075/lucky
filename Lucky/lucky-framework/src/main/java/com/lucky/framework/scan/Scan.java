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

	public static Set<Class<?>> scan(String ...jarFilePath) throws IOException {
		JarFile[] jarFiles=new JarFile[jarFilePath.length];
		URL[] urls=new URL[jarFilePath.length];
		for (int i = 0,j=jarFilePath.length; i <j ; i++) {
			URL url=new URL(jarFilePath[i]);
			urls[i]=url;
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			jarFiles[i]=conn.getJarFile();
		}
		LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls);
		return scan(luckyURLClassLoader,jarFiles);
	}

	private static Set<Class<?>> scan(LuckyURLClassLoader luckyURLClassLoader,JarFile[] jarFilePaths){
		Set<Class<?>> componentClasses=new HashSet<>(225);
		for (JarFile jarFile : jarFilePaths) {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry entry = entrys.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					String clzzName = name.replaceAll("/", "\\.");
					Class<?> fileClass;
					try {
						fileClass = luckyURLClassLoader.loadClass(clzzName);
					}catch (Exception e){
						continue;
					}

					if(fileClass.isAnnotation()){
						continue;
					}
					if(AnnotationUtils.strengthenIsExist(fileClass, Component.class)){
						componentClasses.add(fileClass);
					}
				}
			}
		}
		return componentClasses;
	}

}
