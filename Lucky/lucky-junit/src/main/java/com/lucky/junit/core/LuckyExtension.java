package com.lucky.junit.core;

import com.lucky.aop.core.AopProxyFactory;
import com.lucky.aop.core.ProxyClassFactory;
import com.lucky.aop.core.ProxyObjectFactory;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Injection;
import com.lucky.junit.annotation.LuckyBootTest;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.extension.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/19 上午5:50
 */
public class LuckyExtension implements BeforeAllCallback, AfterAllCallback,
        TestInstancePostProcessor, BeforeEachCallback
        , AfterEachCallback, BeforeTestExecutionCallback,
        AfterTestExecutionCallback, ParameterResolver,TestInstanceFactory{

    private static final RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();
    static {
        String pid = mxb.getName().split("@")[0];
        ThreadContext.put("pid",pid);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        AutoScanApplicationContext.create().close();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        Class<?> testClass = extensionContext.getTestClass().get();
        if(!AnnotationUtils.isExist(testClass, LuckyBootTest.class)){
            AutoScanApplicationContext.create();
        }else{
            Class<?> aClass = AnnotationUtils.get(testClass, LuckyBootTest.class).rootClass();
            aClass=aClass==Void.class?testClass:aClass;
            AutoScanApplicationContext.create(aClass);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }

    @Override
    public void postProcessTestInstance(Object createTest, ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public Object createTestInstance(TestInstanceFactoryContext testInstanceFactoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
        Class<?> testClass = extensionContext.getTestClass().get();
        Object  testInstance= ClassUtils.newObject(testClass);
        Injection.injection(testInstance,"test");
        if(AopProxyFactory.isNeedProxy(testClass)){
            testInstance= ProxyObjectFactory.getProxyFactory().getProxyObject(testInstance);
        }
        return testInstance;

    }
}
