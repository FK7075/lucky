package com.lucky.framework.junit;

import org.junit.runners.model.InitializationError;

public class LuckyRunner extends LuckyJUnit4ClassRunner {

	public LuckyRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

}
