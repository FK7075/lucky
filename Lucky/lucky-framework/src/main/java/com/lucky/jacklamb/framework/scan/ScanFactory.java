package com.lucky.jacklamb.framework.scan;

import java.net.URISyntaxException;

public class ScanFactory {
	
	private static PackageScan pack;
	private static JarScan jar;

	public static Scan createScan(Class<?> applicationBootClass){
		if(PackageScan.class.getClassLoader().getResource("")==null) {
			if(jar==null) {
				try {
					jar= new JarScan(applicationBootClass);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				jar.autoScan();
			}
			return jar;	
		}else {
			if(pack==null) {
				try {
					pack= new PackageScan(applicationBootClass);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				pack.autoScan();
			}
			return pack;
		}
	}

}
