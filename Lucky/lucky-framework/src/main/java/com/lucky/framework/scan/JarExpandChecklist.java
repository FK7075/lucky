package com.lucky.framework.scan;

import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.uitls.reflect.ClassUtils;
import static com.lucky.framework.container.enums.Strategy.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 扩展清单
 * @author fk
 * @version 1.0
 * @date 2020/12/9 0009 10:23
 */
public class JarExpandChecklist {

    private static final Map<String, FusionStrategy> enumsMap;

    static {
        enumsMap=new HashMap<>(9);
        enumsMap.put("REPLACE",REPLACE);
        enumsMap.put("CONTINUE",CONTINUE);
        enumsMap.put("SUPPLEMENT",SUPPLEMENT);
        enumsMap.put("REPLACE_PLUGINS",REPLACE_PLUGINS);
        enumsMap.put("REPLACE_SINGLETON",REPLACE_SINGLETON);
        enumsMap.put("SUPPLEMENT_SINGLETON",SUPPLEMENT_SINGLETON);
        enumsMap.put("SUPPLEMENT_SINGLETON_REPLACE_PLUGINS",SUPPLEMENT_SINGLETON_REPLACE_PLUGINS);
        enumsMap.put("REPLACE_SINGLETON_SUPPLEMENT_PLUGINS",REPLACE_SINGLETON_SUPPLEMENT_PLUGINS);
        enumsMap.put("CONTINUE_SINGLETON_SUPPLEMENT_PLUGINS",CONTINUE_SINGLETON_SUPPLEMENT_PLUGINS);

    }

    private Set<Class<?>> beanClass;
    private Set<IOCBeanFactory> beanFactories;

    public JarExpandChecklist(){
        beanClass=new HashSet<>(50);
        beanFactories=new HashSet<>(10);
    }

    public Set<Class<?>> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Set<Class<?>> beanClass) {
        this.beanClass = beanClass;
    }

    public void addBeanClass(Class<?> beanClass){
        this.beanClass.add(beanClass);
    }

    public Set<IOCBeanFactory> getBeanFactories() {
        return beanFactories;
    }

    public void setBeanFactories(Set<IOCBeanFactory> beanFactories) {
        this.beanFactories = beanFactories;
    }

    public void addBeanFactory(ClassLoader classLoader,String factoryFullName,String fusionStrategyFullName)  {
        try {
            Class<?> aClass = classLoader.loadClass(factoryFullName);
            if(!IOCBeanFactory.class.isAssignableFrom(aClass)){
                throw new RuntimeException();
            }
            IOCBeanFactory beanFactory = (IOCBeanFactory) ClassUtils.newObject(aClass);
            if(enumsMap.containsKey(fusionStrategyFullName.toUpperCase())){
                beanFactory.setFusionStrategy(enumsMap.get(fusionStrategyFullName.toUpperCase()));
                beanFactories.add(beanFactory);
                return;
            }
            Class<?> fusionStrategyClass = classLoader.loadClass(fusionStrategyFullName);
            if(!FusionStrategy.class.isAssignableFrom(fusionStrategyClass)){
                throw new RuntimeException();
            }
            beanFactory.setFusionStrategy((FusionStrategy)ClassUtils.newObject(fusionStrategyClass));
            beanFactories.add(beanFactory);
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }

    }
}
