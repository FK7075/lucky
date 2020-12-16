package com.lucky.framework.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarScan extends Scan {
	
	protected String jarpath;
	private static final Logger log= LoggerFactory.getLogger(JarScan.class);
	protected String prefix;

	public JarScan(Class<?> clzz) throws URISyntaxException {
		super(clzz);
		String allname=clzz.getName();
		String simpleName=clzz.getSimpleName();
		prefix=allname.substring(0, allname.length()-simpleName.length()).replaceAll("\\.", "/");
		jarpath=clzz.getResource("").getPath();
		jarpath=jarpath.substring(5);
		if(jarpath.contains(".jar!")){
			if(jarpath.contains(":")){
				jarpath=jarpath.substring(1, jarpath.indexOf(".jar!")+4);
			}else{
				jarpath=jarpath.substring(0, jarpath.indexOf(".jar!")+4);
			}
		}
	}

	@Override
	public void autoScan() {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarpath);
		} catch (IOException e) {
			throw new JarScanException("找不到jar文件：["+jarpath+"]",e);
		}
		Enumeration<JarEntry> entrys = jarFile.entries();
		try {
			while (entrys.hasMoreElements()) {
				JarEntry entry = entrys.nextElement();
				String name = entry.getName();
				name=name.startsWith("BOOT-INF/classes/")?name.substring(17):name;
				if (name.endsWith(".class") && name.startsWith(prefix)) {
					name = name.substring(0, name.length() - 6);
					String clzzName = name.replaceAll("/", "\\.");
					Class<?> fileClass = loader.loadClass(clzzName);
					load(fileClass);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
