package com.lucky.framework.scan;

import com.lucky.framework.annotation.LuckyBootApplication;
import com.lucky.framework.exception.AddJarExpandException;
import com.lucky.framework.scan.exclusions.Exclusions;
import com.lucky.framework.scan.imports.Imports;
import com.lucky.framework.spi.LuckyFactoryLoader;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/1 上午12:59
 */
public class ImportAndExclusionUtils {

    private static final Logger log= LoggerFactory.getLogger(ImportAndExclusionUtils.class);

    /**
     * 从lucky.factories文件中加载组件[key=com.lucky.framework.scan.SpareComponents]
     * @param loader 类加载器
     * @return
     */
    public Set<Class<?>> loadClassFromLuckyFactories(ClassLoader loader){
        Set<Class<?>> fromLuckyFactoriesClasses=new HashSet<>();
        List<String> spareComponents = LuckyFactoryLoader.loadFactoryNames(SpareComponents.class, loader);
        for (String spareComponent : spareComponents) {
            Class<?> aClass = ClassUtils.forName(spareComponent, loader);
            fromLuckyFactoriesClasses.add(aClass);
        }
        return fromLuckyFactoriesClasses;
    }

    public ImportAndExclusion loadClassFromLuckyBootApplications(ClassLoader loader,LuckyBootApplication ...annotations){
        ImportAndExclusion ie=new ImportAndExclusion();
        for (LuckyBootApplication annotation : annotations) {
            ie.merge(loadClassFromLuckyBootApplication(loader,annotation));
        }
        return ie;
    }

    public ImportAndExclusion loadClassFromLuckyBootApplication(ClassLoader loader,LuckyBootApplication bootAnn){
        Set<Class<?>> jarExpandClasses = loadClassFromLuckyBootApplicationJarExpand(loader, bootAnn.jarExpand());
        ImportAndExclusion ie=loadClassFromLuckyBootApplicationIE(bootAnn);
        ie.addImportClasses(jarExpandClasses);
        return ie;
    }


    public ImportAndExclusion loadClassFromLuckyBootApplicationIE(LuckyBootApplication bootAnn){
        ImportAndExclusion ie=new ImportAndExclusion();
        //获取需要导入的组件类型
        Stream.of(bootAnn.imports()).forEach((ec)->{
            if(Imports.class.isAssignableFrom(ec)){
                Imports imp= (Imports) ClassUtils.newObject(ec);
                ie.addImportClasses(Arrays.asList(imp.imports()));
            }else{
                ie.addImportClass(ec);
            }
        });

        //获取需要排除的组件类型
        Stream.of(bootAnn.exclusions()).forEach((ec)->{
            if(Exclusions.class.isAssignableFrom(ec)){
                Exclusions excs= (Exclusions) ClassUtils.newObject(ec);
                ie.addExclusionClasses(Arrays.asList(excs.exclusions()));
            }else{
                ie.addImportClass(ec);
            }
        });
        return ie;
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

    public ImportAndExclusion loadClassFromImportAndExclusionAnnotation(List<com.lucky.framework.annotation.Imports> imports,
                                                                        List<com.lucky.framework.annotation.Exclusions> exclusions){
    return null;
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
    }
}
