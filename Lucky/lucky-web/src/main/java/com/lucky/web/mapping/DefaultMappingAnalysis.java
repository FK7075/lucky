package com.lucky.web.mapping;

import com.lucky.framework.annotation.Controller;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.web.annotation.*;
import com.lucky.web.enums.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 11:14
 */
public class DefaultMappingAnalysis implements MappingAnalysis{

    private Class<? extends Annotation>[] MAPPING_ANNOTATIONS=
            new Class[]{RequestMapping.class, GetMapping.class, PostMapping.class,
                        PutMapping.class, DeleteMapping.class};

    public MappingCollection analysis(List<Object> controllers){
        MappingCollection mappings=new MappingCollection();
        for (Object controller : controllers) {
            Iterator<Mapping> iterator = analysis(controller).iterator();
            while (iterator.hasNext()){
                mappings.add(iterator.next());
            }
        }
        return mappings;
    }

    @Override
    public MappingCollection analysis(Object controller) {
        MappingCollection mappingCollection=new MappingCollection();
        List<Method> mappingMethods = ClassUtils.getMethodByStrengthenAnnotation(controller.getClass(), RequestMapping.class);
        String controllerUrl=getControllerUrl(controller);
        for (Method method : mappingMethods) {
            String url=controllerUrl+getMethodUrl(method);
            mappingCollection.add(
                    new Mapping(url,controller,
                                method,getRequestMethod(method),
                                getIps(controller,method),
                                getIpSection(controller,method))
            );
        }
        return mappingCollection;
    }

    /**
     * 获取当前Controller类上的URL映射
     * @param controller Controller对象
     * @return URL映射
     */
    protected String getControllerUrl(Object controller){
        Class<?> controllerClass=controller.getClass();
        String controllerUrl;
        if(AnnotationUtils.isExist(controllerClass,RequestMapping.class)){
            controllerUrl=AnnotationUtils.get(controllerClass,RequestMapping.class).value();
        }else{
            controllerUrl= AnnotationUtils.get(controllerClass,Controller.class).value();
        }
        controllerUrl=controllerUrl.startsWith("/")?controllerUrl:"/"+controllerUrl;
        controllerUrl=controllerUrl.endsWith("/")?controllerUrl:controllerUrl+"/";
        return controllerUrl;
    }

    /**
     * 获取当前Controller方法的URL映射
     * @param method Controller方法
     * @return URL映射
     */
    protected String getMethodUrl(Method method){
        Annotation annotation = AnnotationUtils.getByArray(method,MAPPING_ANNOTATIONS);
        String methodUrl= (String) AnnotationUtils.getValue(annotation,"value");
        methodUrl=methodUrl.startsWith("/")?methodUrl.substring(1):methodUrl;
        methodUrl=methodUrl.endsWith("/")?methodUrl.substring(0,methodUrl.length()-1):methodUrl;
        return methodUrl;
    }

    /**
     * 获取当前Controller方法的支持的请求类型
     * @param method Controller方法
     * @return
     */
    protected RequestMethod[] getRequestMethod(Method method){
        return AnnotationUtils.strengthenGet(method, RequestMapping.class).get(0).method();
    }

    /**
     * 获取当前Controller方法支持的IP段
     * @param controller Controller对象
     * @param method Controller方法
     * @return
     */
    protected String[] getIpSection(Object controller,Method method){
        Annotation mappingAnn = AnnotationUtils.getByArray(method, MAPPING_ANNOTATIONS);
        String[] methodIpSection=(String[])AnnotationUtils.getValue(mappingAnn,"ipSection");
        if(!Assert.isEmptyArray(methodIpSection)){
            return methodIpSection;
        }
        Class<?> controllerClass=controller.getClass();
        Controller controllerAnn = AnnotationUtils.strengthenGet(controllerClass, Controller.class).get(0);
        String[] controllerIpSection = controllerAnn.ipSection();
        return controllerIpSection;
    }

    /**
     * 获取当前Controller方法支持的IP
     * @param controller Controller对象
     * @param method Controller方法
     * @return
     */
    protected Set<String> getIps(Object controller,Method method){
        Set<String> ips=new HashSet<>();
        Annotation mappingAnn = AnnotationUtils.getByArray(method, MAPPING_ANNOTATIONS);
        String[] methodIpSection=(String[])AnnotationUtils.getValue(mappingAnn,"ip");
        Class<?> controllerClass=controller.getClass();
        Controller controllerAnn = AnnotationUtils.strengthenGet(controllerClass, Controller.class).get(0);
        String[] controllerIpSection = controllerAnn.ip();
        Stream.of(methodIpSection).forEach(ip->{
            if("localhost".equals(ip)){
                ip="127.0.0.1";
            }
            ips.add(ip);
        });
        Stream.of(controllerIpSection).forEach(ip->{
            if("localhost".equals(ip)){
                ip="127.0.0.1";
            }
            ips.add(ip);
        });
        return ips;
    }
}
