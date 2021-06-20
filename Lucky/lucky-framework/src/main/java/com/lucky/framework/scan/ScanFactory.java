package com.lucky.framework.scan;

import java.net.URISyntaxException;
import java.net.URL;

public class ScanFactory {

//	public static Scan createScan(Class<?> applicationBootClass){
//		Scan scanner = new ComponentAutoScanner(applicationBootClass);
//		scanner.autoScan();
//		return scanner;
//	}

	private static PackageScan pack;
	private static JarScan jar;

	public static Scan createScan(Class<?> applicationBootClass){
		URL resource = ScanFactory.class.getClassLoader().getResource("");
		if(resource!=null&&!resource.getPath().contains(".jar!/")) {
			if(pack==null) {
				try {
					pack= new PackageScan(applicationBootClass);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				pack.autoScan();
			}
			return pack;

		}else {
			if(jar==null) {
				try {
					jar= new JarScan(applicationBootClass);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				jar.autoScan();
			}
			return jar;
		}
	}

}
