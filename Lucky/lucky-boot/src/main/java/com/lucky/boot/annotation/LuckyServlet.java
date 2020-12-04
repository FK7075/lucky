package com.lucky.boot.annotation;

import com.lucky.framework.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "servlet")
public @interface LuckyServlet {
	
	String[] value();

	int loadOnStartup() default -1;
	
}
