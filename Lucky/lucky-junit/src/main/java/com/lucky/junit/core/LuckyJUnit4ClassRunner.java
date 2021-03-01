package com.lucky.junit.core;

import com.lucky.aop.core.AopProxyFactory;
import com.lucky.aop.core.PointRunFactory;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Injection;
import com.lucky.junit.annotation.LuckyBootTest;
import com.lucky.utils.reflect.AnnotationUtils;
import org.apache.logging.log4j.ThreadContext;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class LuckyJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private static final RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();

	static {
		String pid = mxb.getName().split("@")[0];
		ThreadContext.put("pid",pid);
	}
	
	public LuckyJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		if(!AnnotationUtils.isExist(testClass, LuckyBootTest.class)){
			AutoScanApplicationContext.create();
		}else{
			Class<?> aClass = AnnotationUtils.get(testClass, LuckyBootTest.class).rootClass();
			aClass=aClass==Void.class?testClass:aClass;
			AutoScanApplicationContext.create(aClass);
		}

	}

	@Override
	protected Object createTest() throws Exception {
		Object createTest = super.createTest();
		Class<?> aClass = createTest.getClass();
		if(AopProxyFactory.isAgent(aClass)){
			createTest= PointRunFactory.createProxyFactory().getProxy(aClass);
		}
		Injection.injection(createTest,"test");
		return createTest;
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		super.runChild(method, notifier);
        try {
            AutoScanApplicationContext.create().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
