package com.lucky.web.mapping;

import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.web.annotation.*;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;

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



    public UrlMappingCollection analysis(List<Module> controllers){
        UrlMappingCollection mappings=new UrlMappingCollection();
        for (Module controller : controllers) {
            UrlMappingCollection collection = analysis(controller);
            Iterator<UrlMapping> iterator = collection.iterator();
            while (iterator.hasNext()){
                mappings.add(iterator.next());
            }
            Iterator<UrlMapping> runIterator = collection.runIterator();
            while (runIterator.hasNext()){
                mappings.addRun(runIterator.next());
            }
        }
        return mappings;
    }

    public ExceptionMappingCollection exceptionAnalysis(List<Module> controllerAdvices){
        ExceptionMappingCollection exMappings=new ExceptionMappingCollection();
        for (Module controllerAdvice : controllerAdvices) {
            Iterator<ExceptionMapping> iterator = exceptionAnalysis(controllerAdvice).iterator();
            while (iterator.hasNext()){
                exMappings.add(iterator.next());
            }
        }
        return exMappings;
    }

    @Override
    public UrlMappingCollection analysis(Module module) {
        Object controller=module.getComponent();
        Class<?> controllerClass=controller.getClass();
        UrlMappingCollection urlMappingCollection =new UrlMappingCollection();
        List<Method> mappingMethods = ClassUtils.getMethodByStrengthenAnnotation(controllerClass, RequestMapping.class);
        String controllerUrl=getControllerUrl(controllerClass);
        for (Method method : mappingMethods) {
            String url=controllerUrl+getMethodUrl(method);
            urlMappingCollection.add(new UrlMapping(url,module.getId(),
                                module.getType(),controller,
                                method,getRequestMethod(method),
                                getRest(method),getIps(method),
                                getIpSection(method)));
        }
        List<Method> runMethods = ClassUtils.getMethodByAnnotationArrayOR(controllerClass, RUN_ANNOTATIONS);
        for (Method runMethod : runMethods) {
            urlMappingCollection.addRun(controller,runMethod);
        }
        return urlMappingCollection;
    }

    @Override
    public ExceptionMappingCollection exceptionAnalysis(Module module) {
        Object controllerAdvice=module.getComponent();
        ExceptionMappingCollection exceptionMappingCollection=new ExceptionMappingCollection();
        String[] scopes = controllerAdvice.getClass().getAnnotation(ControllerAdvice.class).value();
        List<Method> exceptionMethods = ClassUtils.getMethodByAnnotation(controllerAdvice.getClass(), ExceptionHandler.class);
        for (Method method : exceptionMethods) {
            exceptionMappingCollection.add(new ExceptionMapping(controllerAdvice,method,scopes,
                                            getRest(method),getException(method)));
        }
        return exceptionMappingCollection;
    }

    /**
     * 获取当前Controller类上的URL映射
     * @param controllerClass Controller对象的Class
     * @return URL映射
     */
    protected String getControllerUrl( Class<?> controllerClass){
        String controllerUrl;
        if(AnnotationUtils.isExist(controllerClass,RequestMapping.class)){
            controllerUrl=AnnotationUtils.get(controllerClass,RequestMapping.class).value();
        }else if(AnnotationUtils.isExist(controllerClass,RestController.class)){
            controllerUrl= AnnotationUtils.get(controllerClass,RestController.class).value();
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
     * @param method Controller方法
     * @return
     */
    protected String[] getIpSection(Method method){
        Annotation mappingAnn = AnnotationUtils.getByArray(method, MAPPING_ANNOTATIONS);
        String[] methodIpSection=(String[])AnnotationUtils.getValue(mappingAnn,"ipSection");
        if(!Assert.isEmptyArray(methodIpSection)){
            return methodIpSection;
        }
        Class<?> controllerClass=method.getDeclaringClass();
        Annotation controllerAnnotation = AnnotationUtils.getByArray(controllerClass, CONTROLLER_ANNOTATIONS);
        return (String[]) AnnotationUtils.getValue(controllerAnnotation,"ipSection");
    }

    /**
     * 获取当前Controller方法支持的IP
     * @param method Controller方法
     * @return
     */
    protected Set<String> getIps(Method method){
        Set<String> ips=new HashSet<>();
        Annotation mappingAnn = AnnotationUtils.getByArray(method, MAPPING_ANNOTATIONS);
        String[] methodIpSection=(String[])AnnotationUtils.getValue(mappingAnn,"ip");
        Class<?> controllerClass=method.getDeclaringClass();
        Annotation controllerAnnotation = AnnotationUtils.getByArray(controllerClass, CONTROLLER_ANNOTATIONS);
        String[] controllerIpSection = (String[]) AnnotationUtils.getValue(controllerAnnotation,"ip");
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

    /**
     * 获取当前Controller的响应处理方式
     * @param method Controller方法
     * @return
     */
    protected Rest getRest(Method method){
        if(AnnotationUtils.isExist(method,ResponseBody.class)){
            return AnnotationUtils.get(method,ResponseBody.class).value();
        }
        Class<?> controllerClass = method.getDeclaringClass();
        if(AnnotationUtils.isExist(controllerClass,Controller.class)){
            return Rest.NO;
        }
        if(AnnotationUtils.isExist(controllerClass,RestController.class)){
            return AnnotationUtils.get(controllerClass,RestController.class).rest();
        }
        if(AnnotationUtils.isExist(controllerClass,ControllerAdvice.class)){
            return AnnotationUtils.get(controllerClass,ControllerAdvice.class).rest();
        }
        return Rest.NO;
    }

    public Class<? extends Throwable>[] getException(Method method){
        return AnnotationUtils.get(method,ExceptionHandler.class).value();
    }
}
