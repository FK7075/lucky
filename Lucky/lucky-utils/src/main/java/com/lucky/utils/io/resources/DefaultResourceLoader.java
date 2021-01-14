//package com.lucky.utils.io.resources;
//
//import com.lucky.utils.annotation.Nullable;
//import com.lucky.utils.base.Assert;
//import com.lucky.utils.reflect.ClassUtils;
//
//import java.util.LinkedHashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author fk
// * @version 1.0
// * @date 2021/1/14 0014 15:55
// */
//public class DefaultResourceLoader implements ResourceLoader{
//
//    @Nullable
//    private ClassLoader classLoader;
//    private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);
//    private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);
//
//    public DefaultResourceLoader(){
//
//    }
//
//    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }
//
//    public void setClassLoader(@Nullable ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }
//
//    @Override
//    public ClassLoader getClassLoader() {
//        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
//    }
//
//    @Override
//    public Resource getResource(String location) {
//        Assert.notNull(location, "Location must not be null");
//        return null;
//    }
//
//}
