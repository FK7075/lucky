package com.lucky.framework.junit;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.annotation.LuckyApplicationTest;
import com.lucky.framework.container.Injection;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;

public class LuckyJUnit4ClassRunner extends BlockJUnit4ClassRunner {
	
	
	public LuckyJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		if(!AnnotationUtils.isExist(testClass, LuckyApplicationTest.class)){
			new AutoScanApplicationContext();
		}else{
			new AutoScanApplicationContext(AnnotationUtils.get(testClass, LuckyApplicationTest.class).rootClass());
		}

	}

	@Override
	protected Object createTest() throws Exception {
		Object createTest = super.createTest();
		Injection.injection(createTest,"test");
		return createTest;
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		super.runChild(method, notifier);
	}
}
