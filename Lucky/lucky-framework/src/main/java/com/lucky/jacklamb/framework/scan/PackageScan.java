package com.lucky.jacklamb.framework.scan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 不做配置时的默认包扫描
 * @author fk-7075
 *
 */
public class PackageScan extends Scan {

	private static final Logger log= LogManager.getLogger(PackageScan.class);
	//Wim: E:/project  Mac: /User/loca
	private String projectPath;
	//Win: E:/project/ Mac: /User/loca/
	private String fileProjectPath;
	private LuckyClassLoader luckyClassLoader;

	public PackageScan(Class<?> applicationBootClass) throws URISyntaxException {
		super(applicationBootClass);
		projectPath=applicationBootClass.getResource("").toURI().getPath();
		if(projectPath.endsWith("/classes/")) {
			projectPath=projectPath.substring(0,projectPath.length()-8);
		}else if(projectPath.endsWith("/test-classes/")) {
			projectPath=projectPath.substring(0,projectPath.length()-13);
		}
		if(projectPath.contains(":")){
			fileProjectPath=projectPath.substring(1);
			projectPath=projectPath.replaceAll("\\\\", "/").substring(1,projectPath.length()-1);
		}else{
			fileProjectPath=projectPath;
			projectPath=projectPath.replaceAll("\\\\", "/").substring(0,projectPath.length()-1);
		}
	}

	@Override
	public void autoScan() {
		try {
			fileScan(fileProjectPath);
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
		String className=file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(fileProjectPath, "").replaceAll("/", "\\.");
		className=className.substring(0,className.length()-6);
		Class<?> fileClass;
		if(className.startsWith("classes.")) {
			className=className.substring(8);
			fileClass=Class.forName(className);
		}else if(className.startsWith("test-classes.")) {
			luckyClassLoader=new LuckyClassLoader(fileProjectPath+File.separator+"test-classes");
			className=className.substring(13);
			fileClass=luckyClassLoader.loadClass(className);
		}else{
			fileClass=Class.forName(className);
		}
		return  fileClass;
	}
}


