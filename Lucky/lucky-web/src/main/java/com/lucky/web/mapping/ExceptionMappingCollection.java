package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.ExceptionUtils;
import com.lucky.web.exception.RepeatUrlMappingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/24 9:15
 */
public class ExceptionMappingCollection {
    private static final Logger log= LogManager.getLogger("c.l.w.mapping.ExceptionMappingCollection");

    /** 异常处理器的集合【ExceptionHandler集】*/
    private List<ExceptionMapping> list;
    /** ExceptionHandler扩展*/
    private Map<String,ExceptionMappingCollection> expandMap;
    /** 被逻辑删除的扩展名*/
    private Set<String> deleteExpand;

    public ExceptionMappingCollection(){
        list=new ArrayList<>(10);
        expandMap=new HashMap<>();
        deleteExpand=new HashSet<>();
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

    public void clear() {
        list.clear();
    }

    public Map<String, ExceptionMappingCollection> getExpandMap() {
        return expandMap;
    }

    public void setExpandMap(Map<String, ExceptionMappingCollection> expandMap) {
        this.expandMap = expandMap;
    }

    public Set<String> getDeleteExpand() {
        return deleteExpand;
    }

    public void setDeleteExpand(Set<String> deleteExpand) {
        this.deleteExpand = deleteExpand;
    }

    /**
     * 排斥检查，检查不通过会抛出异常：RepeatDefinitionExceptionHandlerException
     * @param em 待检查的异常处理映射
     */
    public void exclusionCheck(ExceptionMapping em){
        for (ExceptionMapping cem : list) {
            cem.isRepel(em);
        }
    }

    /**
     * 添加一个异常处理器
     * @param em 异常处理器
     * @param isLog 是否打印日志
     * @return
     */
    public boolean add(ExceptionMapping em,boolean isLog) {
        if(Assert.isNull(em)){
            return false;
        }
        exclusionCheck(em);
        list.add(em);
        if(isLog){
            log.info("ExceptionHandler `Scopes=[{}]` , Exception={}",
                    em.getStrScopes(),Arrays.toString(em.getExceptions()));
        }
        return true;
    }

    /**
     * 添加一个异常处理器,并打印日志
     * @param em 异常处理器
     * @return
     */
    public boolean add(ExceptionMapping em){
        return add(em,true);
    }

    /***
     * 添加一个ExceptionHandler映射集的扩展
     * @param expandName 扩展名
     * @param expand ExceptionHandler映射集
     * @return
     */
    public boolean addExpand(String expandName,ExceptionMappingCollection expand){
        if(deleteExpand.contains(expandName)){
            deleteExpand.remove(expandName);
            return true;
        }
        if(expandMap.containsKey(expandName)){
            log.warn("扩展名为 `{}` 的ExceptionHandler扩展集已经存在,故此次添加将不会生效！",expandName);
            return false;
        }
        Iterator<ExceptionMapping> iterator = expand.iterator();
        //添加前的重复映射校验
        while (iterator.hasNext()){
            ExceptionMapping urlMapping = iterator.next();
            exclusionCheck(urlMapping);
            expandMap.values().stream().forEach(umc->umc.exclusionCheck(urlMapping));
        }
        expandMap.put(expandName,expand);
        log.info("ExceptionHandler扩展集 `{}` 添加成功！ExceptionHandler处理器总数为：{}",expandName,expand.size());
        return true;
    }

    /**
     * 删除一个ExceptionHandler集的扩【逻辑删除】
     * @param expandName 扩展名
     * @return
     */
    public boolean deleteExpand(String expandName){
        if(!expandMap.containsKey(expandName)){
            log.warn("不存在扩展名为 `{}` 的ExceptionHandler扩展集,删除操作无效！",expandName);
            return false;
        }
        if(deleteExpand.contains(expandName)){
            return false;
        }
        deleteExpand.add(expandName);
        log.info("ExceptionHandler扩展集 `{}` 已删除！",expandName);
        return true;
    }

    public ExceptionMapping findExceptionMapping(UrlMapping urlMapping, Throwable ex){
        ExceptionMapping exceptionMapping = getExceptionMapping(urlMapping, ex);
        if(Assert.isNotNull(exceptionMapping)){
            return exceptionMapping;
        }
        Set<ExceptionMappingCollection> expandCollectSet = expandMap.keySet()
                .stream()
                .filter(k -> !deleteExpand.contains(k))
                .map(expandMap::get).collect(Collectors.toSet());
        for (ExceptionMappingCollection exceptionMappingCollection : expandCollectSet) {
            ExceptionMapping mapping = exceptionMappingCollection.getExceptionMapping(urlMapping, ex);
            if(Assert.isNotNull(mapping)){
                return mapping;
            }
        }

        ExceptionMapping globalExceptionMapping = getGlobalExceptionMapping(ex);
        if(Assert.isNotNull(globalExceptionMapping)){
            return globalExceptionMapping;
        }

        for (ExceptionMappingCollection exceptionMappingCollection : expandCollectSet) {
            ExceptionMapping mapping = exceptionMappingCollection.getGlobalExceptionMapping(ex);
            if (Assert.isNotNull(mapping)) {
                return mapping;
            }
        }
        return null;

    }

    /***
     * 根据一个Mapping映射与一个具体的异常信息得到一个唯一的异常映射ExceptionMapping
     * @param urlMapping 当前请求的具体映射
     * @param ex 执行请求时出现的异常
     * @return
     */
    public ExceptionMapping getExceptionMapping(UrlMapping urlMapping, Throwable ex){
        String iocId= urlMapping.getIocId();
        String methosId=iocId+"."+ urlMapping.getMapping().getName();
        Map<Class<? extends Throwable>, ExceptionMapping> methodIdExMap = findByMethodId(methosId, ex);
        if(!methodIdExMap.isEmpty()){
            return locate(methodIdExMap,ex);
        }else{
            Map<Class<? extends Throwable>, ExceptionMapping> iocIdIdExMap = findByIocId(iocId, ex);
            if(!iocIdIdExMap.isEmpty()){
                return locate(iocIdIdExMap,ex);
            }else{
//                Map<Class<? extends Throwable>, ExceptionMapping> globalExMap = findGlobal(ex);
//                if(!globalExMap.isEmpty()){
//                    return locate(globalExMap,ex);
//                }
                return null;
            }
        }
    }

    public ExceptionMapping getGlobalExceptionMapping(Throwable ex){
        Map<Class<? extends Throwable>, ExceptionMapping> globalExMap = findGlobal(ex);
        if(!globalExMap.isEmpty()){
            return locate(globalExMap,ex);
        }
        return null;
    }

    /**
     * 定位异常处理器
     * @param exMap 可以处理该异常的异常处理器的Map
     * @param ex 当前异常
     * @return
     */
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

    /**
     * 将传入的异常处理集融合到当前的异常处理集集中
     * @param collection
     * @return
     */
    public boolean merge(ExceptionMappingCollection collection){
        Iterator<ExceptionMapping> iterator = collection.iterator();
        while (iterator.hasNext()){
            add(iterator.next(),false);
        }
        return true;
    }
}
