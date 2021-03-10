package com.lucky.aop.proxy;

import com.lucky.aop.core.*;
import com.lucky.utils.proxy.LuckyInvocationHandler;
import com.lucky.utils.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 15:57
 */
public class LuckyAopInvocationHandler extends LuckyInvocationHandler {

    private final List<PointRun> pointRuns;//关于某一个类的所有增强的执行节点
    private static final Set<InjectionAopPoint> injectionAopPoints= AopProxyFactory.injectionAopPointSet;

    /**
     * 回调函数构造器，得到一个真实对象的的所有执行方法(MethodRun)和环绕执行节点集合(PointRun)，
     * 根据实际情况为真实对象的每一个需要被增强的方法产生一个特定的回调策略
     * @param pointRuns 环绕执行节点集合(可变参形式传入)
     */
    public LuckyAopInvocationHandler(Object target,PointRun...pointRuns) {
        super(target);
        this.pointRuns=new ArrayList<>();
        Stream.of(pointRuns).forEach(this.pointRuns::add);
    }

    /**
     * 回调函数构造器，得到一个真实对象的的所有执行方法(MethodRun)和环绕执行链(PointRun)，
     * 根据实际情况为真实对象的每一个需要被增强的方法产生一个特定的回调策略
     * @param pointRuns 环绕执行节点集合(集合参形式传入)
     */
    public LuckyAopInvocationHandler(Object target,List<PointRun> pointRuns) {
        super(target);
        this.pointRuns=new ArrayList<>();
        this.pointRuns.addAll(pointRuns);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        if(params==null){
            params=new Object[0];
        }
        //Object方法不执行代理
        if(MethodUtils.isObjectMethod(method)){
            return MethodUtils.invoke(getTarget(),method,params);
        }

        //如果是final方法则执行原方法
        if(Modifier.isFinal(method.getModifiers())){
            return MethodUtils.invoke(getTarget(),method,params);
        }
        final List<AopPoint> points=new ArrayList<>();
        TargetMethodSignature targetMethodSignature
                =new TargetMethodSignature(proxy,getTarget(),method,params);

        //得到所有注入式的环绕增强节点(IAOP)
        injectionAopPoints.forEach((iap)->{
            if(iap.pointCutMethod(getTarget().getClass().getSuperclass(),method)){
                iap.init(targetMethodSignature);
                points.add(iap);
            }
        });
        //得到所有自定义的的环绕增强节点
        pointRuns.stream().filter(a->a.methodExamine(getTarget().getClass(),method)).forEach((a)->{
            AopPoint p=a.getPoint();
            p.init(targetMethodSignature);
            points.add(p);
        });
        //将所的环绕增强节点根据优先级排序后组成一个执行链
        AopChain chain=new JDKAopChain(points.stream()
                .sorted(Comparator.comparing(AopPoint::getPriority))
                .collect(Collectors.toList()),getTarget(),params,method);
        Object resule;

        //执行增强策略
        resule= chain.proceed();
        return resule;
    }
}
