package com.lucky.framework.scan;

import com.lucky.framework.annotation.Configuration;
import com.lucky.framework.annotation.LuckyBootApplication;
import com.lucky.framework.exception.AddJarExpandException;
import com.lucky.framework.scan.exclusions.Exclusions;
import com.lucky.framework.scan.imports.Imports;
import com.lucky.framework.spi.LuckyFactoryLoader;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/1 上午12:59
 */
public class ImportAndExclusionUtils {

    private static final Logger log= LoggerFactory.getLogger("c.l.f.scan.ImportAndExclusionUtils");
    private final ImportAndExclusion ie;

    public ImportAndExclusionUtils(ClassLoader loader,Class<?> bootClass){
        ie=new ImportAndExclusion();
        ie.addImportClasses(loadClassFromLuckyFactories(loader));
        if(bootClass!=null){
            List<LuckyBootApplication> luckyBootApplications = AnnotationUtils.strengthenGet(bootClass, LuckyBootApplication.class);
            List<com.lucky.framework.annotation.Imports> imports = AnnotationUtils.strengthenGet(bootClass, com.lucky.framework.annotation.Imports.class);
            List<com.lucky.framework.annotation.Exclusions> exclusions = AnnotationUtils.strengthenGet(bootClass, com.lucky.framework.annotation.Exclusions.class);
            List<com.lucky.framework.annotation.JarExpand> jarExpands = AnnotationUtils.strengthenGet(bootClass, com.lucky.framework.annotation.JarExpand.class);
            ie.merge(loadClassFromLuckyBootApplications(loader,luckyBootApplications));
            ie.merge(loadClassFromImportAndExclusionAnnotation(imports,exclusions));
            ie.addImportClasses(loadClassFromJarExpandAnnotations(loader,jarExpands));
        }
    }

    public ImportAndExclusion getIe() {
        return ie;
    }

    /**
     * 从lucky.factories文件中加载组件
     * @param loader 类加载器
     * @return
     */
    public Set<Class<?>> loadClassFromLuckyFactories(ClassLoader loader){
        Set<Class<?>> fromLuckyFactoriesClasses=new HashSet<>();
        Set<Class<?>> spareComponentClasses = LuckyFactoryLoader.loadFactoryNames(SpareComponents.class, loader)
                .stream().map(cs->ClassUtils.forName(cs,loader)).collect(Collectors.toSet());
        Set<Class<?>> configurationClasses = LuckyFactoryLoader.loadFactoryClasses(Configuration.class, loader);
        fromLuckyFactoriesClasses.addAll(spareComponentClasses);
        fromLuckyFactoriesClasses.addAll(configurationClasses);
        return fromLuckyFactoriesClasses;
    }

    public ImportAndExclusion loadClassFromLuckyBootApplications(ClassLoader loader,List<LuckyBootApplication> annotations){
        ImportAndExclusion ie=new ImportAndExclusion();
        for (LuckyBootApplication annotation : annotations) {
            ie.merge(loadClassFromLuckyBootApplication(loader,annotation));
        }
        return ie;
    }

    /**
     * 从启动类获取组件
     *  1.jarExpand属性加载外部jar包
     *  2.imports属性盗取外部组件
     *  3.exclusions属性设置排除组件
     * @param loader
     * @param bootAnn
     * @return
     */
    public ImportAndExclusion loadClassFromLuckyBootApplication(ClassLoader loader,LuckyBootApplication bootAnn){
        Set<Class<?>> jarExpandClasses = loadClassFromLuckyBootApplicationJarExpand(loader, bootAnn.jarExpand());
        ImportAndExclusion ie=loadClassFromLuckyBootApplicationIE(bootAnn);
        ie.addImportClasses(jarExpandClasses);
        return ie;
    }


    public ImportAndExclusion loadClassFromLuckyBootApplicationIE(LuckyBootApplication bootAnn){
        return importAndExclusionClassTo(bootAnn.imports(),bootAnn.exclusions());
    }

    private ImportAndExclusion importAndExclusionClassTo(Class<?>[] importClasses,Class<?>[] exclusionClasses){
        ImportAndExclusion ie=new ImportAndExclusion();
        //获取需要导入的组件类型
        Stream.of(importClasses).forEach((ec)->{
            if(Imports.class.isAssignableFrom(ec)){
                Imports imp= (Imports) ClassUtils.newObject(ec);
                ie.addImportClasses(Arrays.asList(imp.imports()));
            }else{
                ie.addImportClass(ec);
            }
        });

        //获取需要排除的组件类型
        Stream.of(exclusionClasses).forEach((ec)->{
            if(Exclusions.class.isAssignableFrom(ec)){
                Exclusions excs= (Exclusions) ClassUtils.newObject(ec);
                ie.addExclusionClasses(Arrays.asList(excs.exclusions()));
            }else{
                ie.addExclusionClass(ec);
            }
        });
        return ie;
    }

    public Set<Class<?>> loadClassFromJarExpandAnnotations(ClassLoader loader,List<com.lucky.framework.annotation.JarExpand> jarExpands){
        Set<Class<?>> classes=new HashSet<>();
        for (com.lucky.framework.annotation.JarExpand jarExpand : jarExpands) {
            classes.addAll(loadClassFromLuckyBootApplicationJarExpand(loader,jarExpand.value()));
        }
        return classes;
    }

    public Set<Class<?>> loadClassFromLuckyBootApplicationJarExpand(ClassLoader loader,String jarExpand){
        Set<Class<?>> jarExpandClasses=new HashSet<>();
        List<JarExpand> jars=new ArrayList<>();
        if(!Assert.isBlankString(jarExpand)){
            jars.addAll(JarExpand.getJarExpandByJsonFile(jarExpand));
        }
        URL[] urls=new URL[1];
        for (JarExpand jar : jars) {
            log.info("Load external Jar groupId= `{}` , jarPath= `{}`",jar.getGroupId(),jar.getJarPath());
            try {
                urls[0]=new URL(jar.getJarPath());
            }catch (MalformedURLException e){
                throw new AddJarExpandException(jar.getJarPath());
            }

            LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls,loader);
            jarExpandClasses.addAll(luckyURLClassLoader.getComponentClass(jar.getGroupId()).getBeanClass());
        }
        return jarExpandClasses;
    }

    /**
     * 从@Imports注解获取外部组件
     * 从@Exclusions注解获取需要排除的组件
     * @param imports 所有的@Imports注解实例
     * @param exclusions 所有@Exclusions注解实例
     * @return
     */
    public ImportAndExclusion loadClassFromImportAndExclusionAnnotation(List<com.lucky.framework.annotation.Imports> imports,
                                                                        List<com.lucky.framework.annotation.Exclusions> exclusions){
        ImportAndExclusion ie=new ImportAndExclusion();
        imports.stream().map(im->importAndExclusionClassTo(im.value(),new Class[]{})).forEach(ie::merge);
        exclusions.stream().map(ex->importAndExclusionClassTo(new Class[]{},ex.value())).forEach(ie::merge);
        return ie;
    }

    public static class ImportAndExclusion{
        private Set<Class<?>> importClasses;
        private Set<Class<?>> exclusionClasses;

        public ImportAndExclusion(){
            importClasses=new HashSet<>();
            exclusionClasses=new HashSet<>();
        }

        public Set<Class<?>> getImportClasses() {
            return importClasses;
        }

        public void addImportClasses(Collection<Class<?>> importClasses){
            this.importClasses.addAll(importClasses);
        }

        public void addImportClass(Class<?> aClass){
            this.importClasses.add(aClass);
        }

        public void setImportClasses(Set<Class<?>> importClasses) {
            this.importClasses = importClasses;
        }

        public Set<Class<?>> getExclusionClasses() {
            return exclusionClasses;
        }

        public void setExclusionClasses(Set<Class<?>> exclusionClasses) {
            this.exclusionClasses = exclusionClasses;
        }

        public void addExclusionClasses(Collection<Class<?>> exclusionClasses) {
            this.exclusionClasses.addAll(exclusionClasses);
        }

        public void addExclusionClass(Class<?> aClass) {
            this.exclusionClasses.add(aClass);
        }

        public void merge(ImportAndExclusion ie){
            importClasses.addAll(ie.getImportClasses());
            exclusionClasses.addAll(ie.getExclusionClasses());
        }

        public Set<Class<?>> subtraction(){
            return importClasses.stream().filter(c->!exclusionClasses.contains(c)).collect(Collectors.toSet());
        }

        public void log(){
            if(!Assert.isEmptyCollection(importClasses)){
                log.info("Import Classes `{}`",importClasses);
            }
            if(!Assert.isEmptyCollection(exclusionClasses)){
                log.info("Exclusions Classes `{}`",exclusionClasses);
            }
        }
    }
}
