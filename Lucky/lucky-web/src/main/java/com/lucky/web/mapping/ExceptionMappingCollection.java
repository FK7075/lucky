package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.ExceptionUtils;
import com.lucky.web.exception.RepeatUrlMappingException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public ExceptionMapping getExceptionMapping(Mapping mapping,Throwable ex){
        String iocId=mapping.getIocId();
        String methosId=iocId+"."+mapping.getMapping().getName();
        return null;
    }

    private List<ExceptionMapping> findByIocId(String iocId,Throwable ex){
       return null;
    }

    private List<ExceptionMapping> findByMethodId(String methodId,Throwable ex){
        List<ExceptionMapping> midmap=new ArrayList<>();
//        list.stream().filter(em->Assert.inArray(em.getScopes(),methodId)).forEach((em)->{
//            ExceptionUtils.getExceptionFamily(ex.getClass());
//            if(ExceptionUtils.contained())
//        });

        return midmap;
    }

}
