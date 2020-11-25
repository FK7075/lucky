package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/24 9:15
 */
public class ExceptionMappingCollection {

    private List<ExceptionMapping> list;

    public ExceptionMappingCollection(){
        list=new ArrayList<>(10);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<ExceptionMapping> iterator() {
        return list.iterator();
    }

    public boolean add(ExceptionMapping em) {
        if(Assert.isNull(em)){
            return false;
        }
        for (ExceptionMapping cem : list) {
            cem.isRepel(em);
        }
        list.add(em);
        return true;
    }

    public void clear() {
        list.clear();
    }

    /***
     * 根据一个Mapping映射与一个具体的异常信息得到一个唯一的异常映射ExceptionMapping
     * @param mapping 当前请求的具体映射
     * @param ex 执行请求时出现的异常
     * @return
     */
    public ExceptionMapping getExceptionMapping(Mapping mapping,Throwable ex){
        String iocId=mapping.getIocId();
        String methosId=iocId+"."+mapping.getMapping().getName();
        Map<Class<? extends Throwable>, ExceptionMapping> methodIdExMap = findByMethodId(methosId, ex);
        if(!methodIdExMap.isEmpty()){
            return locate(methodIdExMap,ex);
        }else{
            Map<Class<? extends Throwable>, ExceptionMapping> iocIdIdExMap = findByIocId(iocId, ex);
            if(!iocIdIdExMap.isEmpty()){
                return locate(iocIdIdExMap,ex);
            }else{
                Map<Class<? extends Throwable>, ExceptionMapping> globalExMap = findGlobal(ex);
                if(!globalExMap.isEmpty()){
                    return locate(globalExMap,ex);
                }
                return null;
            }
        }
    }

    private ExceptionMapping locate(Map<Class<? extends Throwable>,ExceptionMapping> exMap,Throwable ex){
        List<Class<? extends Throwable>> exceptionFamily = ExceptionUtils.getExceptionFamily(ex.getClass());
        for (Class<? extends Throwable> exClass : exceptionFamily) {
            if(exMap.containsKey(exClass)){
                return exMap.get(exClass);
            }
        }
        return null;
    }

    /**
     * 使用全局异常处理器并过滤
     * @param ex 当前异常
     * @return
     */
    private Map<Class<? extends Throwable>,ExceptionMapping> findGlobal(Throwable ex){
        List<ExceptionMapping> methodIdEmList = list.stream()
                .filter(em -> Assert.isEmptyArray(em.getScopes()))
                .collect(Collectors.toList());
        return unfoldFilter(methodIdEmList,ex);
    }

    /**
     * 使用IocId查找、过滤映射
     * @param iocId IOCid
     * @param ex 当前异常
     * @return
     */
    private Map<Class<? extends Throwable>,ExceptionMapping> findByIocId(String iocId,Throwable ex){
        List<ExceptionMapping> methodIdEmList = list.stream()
                .filter(em -> Assert.inArray(em.getScopes(), iocId))
                .collect(Collectors.toList());
        return unfoldFilter(methodIdEmList,ex);
    }

    /**
     * 使用methodId查找、过滤映射
     * @param methodId IOCid
     * @param ex 当前异常
     * @return
     */
    private Map<Class<? extends Throwable>,ExceptionMapping> findByMethodId(String methodId,Throwable ex){
        List<ExceptionMapping> methodIdEmList = list.stream()
                .filter(em -> Assert.inArray(em.getScopes(), methodId))
                .collect(Collectors.toList());
        return unfoldFilter(methodIdEmList,ex);
    }

    /**
     * 将一个ExceptionMapping集合展开成一个KEY为ThrowableClass，VALUE为ExceptionMapping的Map映射
     * 并过滤掉不满足指定异常的KEY-VALUE
     * @param emList ExceptionMapping集合
     * @param ex 过滤标准
     * @return
     */
    private Map<Class<? extends Throwable>,ExceptionMapping> unfoldFilter(List<ExceptionMapping> emList,Throwable ex){
        Map<Class<? extends Throwable>,ExceptionMapping> exMap=new HashMap<>();
        Map<Class<? extends Throwable>, ExceptionMapping> unfoldMap = unfold(emList);
        List<Class<? extends Throwable>> exceptionFamily = ExceptionUtils.getExceptionFamily(ex.getClass());
        for (Class<? extends Throwable> exClass : exceptionFamily) {
            if(unfoldMap.containsKey(exClass)){
                exMap.put(exClass,unfoldMap.get(exClass));
            }
        }
        return exMap;
    }

    /**
     * 将一个ExceptionMapping集合展开成一个KEY为ThrowableClass，VALUE为ExceptionMapping的Map映射
     * @param emList ExceptionMapping集合
     * @return
     */
    private Map<Class<? extends Throwable>,ExceptionMapping> unfold(List<ExceptionMapping> emList){
        Map<Class<? extends Throwable>,ExceptionMapping> exMap=new HashMap<>();
        emList.stream().forEach(em->{
            Class<? extends Throwable>[] exceptions = em.getExceptions();
            for (Class<? extends Throwable> exception : exceptions) {
                exMap.put(exception,em);
            }
        });
        return exMap;
    }
}
