package com.lucky.aop.proxy;

import com.lucky.aop.core.*;
import com.lucky.framework.container.Injection;
import com.lucky.utils.proxy.LuckyMethodInterceptor;
import com.lucky.utils.reflect.MethodUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 15:42
 */
public class LuckyAopObjectProxyMethodInterceptor extends LuckyMethodInterceptor {

    private final List<PointRun> pointRuns;//关于某一个类的所有增强的执行节点
    private static final Set<InjectionAopPoint> injectionAopPoints= AopProxyFactory.injectionAopPointSet;

    /**
     * 回调函数构造器，得到一个真实对象的的所有执行方法(MethodRun)和环绕执行节点集合(PointRun)，
     * 根据实际情况为真实对象的每一个需要被增强的方法产生一个特定的回调策略
     * @param pointRuns 环绕执行节点集合(可变参形式传入)
     */
    public LuckyAopObjectProxyMethodInterceptor(Object target,PointRun...pointRuns) {
        super(target);
        this.pointRuns=new ArrayList<>();
        this.pointRuns.addAll(Arrays.asList(pointRuns));
    }

    /**
     * 回调函数构造器，得到一个真实对象的的所有执行方法(MethodRun)和环绕执行链(PointRun)，
     * 根据实际情况为真实对象的每一个需要被增强的方法产生一个特定的回调策略
     * @param pointRuns 环绕执行节点集合(集合参形式传入)
     */
    public LuckyAopObjectProxyMethodInterceptor(Object target,List<PointRun> pointRuns) {
        super(target);
        this.pointRuns=new ArrayList<>();
        this.pointRuns.addAll(pointRuns);
    }
    @Override
    public Object intercept(Object proxy, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        //Object方法不执行代理
        if(MethodUtils.isObjectMethod(method)){
            return methodProxy.invoke(getTarget(),params);
        }

        //如果是final方法则执行原方法
        if(Modifier.isFinal(method.getModifiers())){
            return methodProxy.invoke(getTarget(),params);
        }
        final List<AopPoint> points=new ArrayList<>();
        TargetMethodSignature targetMethodSignature
                =new TargetMethodSignature(proxy,getTarget(),method,params);

        //得到所有注入式的环绕增强节点(IAOP)
        injectionAopPoints.forEach((iap)->{
            if(iap.pointCutMethod(getTarget().getClass(),method)){
                points.add(iap.cloneObject(targetMethodSignature));
            }
        });
        //得到所有自定义的的环绕增强节点
        pointRuns.stream().filter(a->a.methodExamine(getTarget().getClass(),method)).forEach((a)->{
            AopPoint p=a.getPoint();
            points.add(p.cloneObject(targetMethodSignature));
        });

        //将所的环绕增强节点根据优先级排序后组成一个执行链
        CglibAopChain chain=new CglibAopChain(points.stream()
                .sorted(Comparator.comparing(AopPoint::getPriority))
                .collect(Collectors.toList()),getTarget(),params,methodProxy,method);
        Object resule;

        chain.isObjectProxy(true);
        //执行增强策略
        resule= chain.proceed();
        return resule;
    }
}
