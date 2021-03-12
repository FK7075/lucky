package com.lucky.v2.aop;

import com.lucky.v2.aop.advisor.Advisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 11:37
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(JdkDynamicAopProxy.class);

    private String beanName;
    private Object target;
    private List<Advisor> matchAdvisors;

    private BeanFactory beanFactory;

    public JdkDynamicAopProxy(String beanName, Object target, List<Advisor> matchAdvisors, BeanFactory beanFactory) {
        super();
        this.beanName = beanName;
        this.target = target;
        this.matchAdvisors = matchAdvisors;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getProxy() {
        return this.getProxy(target.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("为" + target + "创建代理。");
        }
        return Proxy.newProxyInstance(classLoader, target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return AopProxyUtils.applyAdvices(target, method, args, matchAdvisors, proxy, beanFactory);
    }
}
