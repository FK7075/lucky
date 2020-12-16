package com.lucky.framework.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 不做配置时的默认包扫描
 * @author fk-7075
 *
 */
public class PackageScan extends Scan {

	private static final Logger log= LoggerFactory.getLogger(PackageScan.class);
	//Wim: E:/project  Mac: /User/loca
	private String rootPath;
	//Win: E:/project/ Mac: /User/loca/
	private String projectPath;
	private LuckyClassLoader luckyClassLoader;

	public PackageScan(Class<?> applicationBootClass) throws URISyntaxException {
		super(applicationBootClass);
		projectPath=PackageScan.class.getClassLoader().getResource("").toURI().getPath();
		projectPath=projectPath.contains(":")?projectPath.substring(1):projectPath;
		projectPath=projectPath.replaceAll("\\\\","/");
		if(applicationBootClass==null){
			rootPath=PackageScan.class.getClassLoader().getResource("").toURI().getPath();
		}else {
			rootPath=applicationBootClass.getResource("").toURI().getPath();
		}
		rootPath=rootPath.replaceAll("/test-classes/","/classes/");
		projectPath=projectPath.replaceAll("/test-classes/","/classes/");
	}

	@Override
	public void autoScan() {
		try {
			fileScan(rootPath);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void fileScan(String path) throws ClassNotFoundException {
		File bin=new File(path);
		File[] listFiles = bin.listFiles();
		for(File file:listFiles) {
			if(file.isDirectory()) {
				fileScan(path+"/"+file.getName());
			}else if(file.getAbsolutePath().endsWith(".class")) {
				load(getFileClass(file));
			}
		}
	}

	private Class<?> getFileClass(File file) throws ClassNotFoundException {
		String className=file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(projectPath, "").replaceAll("/", "\\.");
		className=className.substring(0,className.length()-6);
		Class<?> fileClass=loader.loadClass(className);
		return  fileClass;
	}
}


