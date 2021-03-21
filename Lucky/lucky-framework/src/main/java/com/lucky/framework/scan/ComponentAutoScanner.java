package com.lucky.framework.scan;

import com.lucky.framework.exception.LuckyIOException;
import com.lucky.utils.fileload.Resource;
import com.lucky.utils.fileload.resourceimpl.PathMatchingResourcePatternResolver;
import com.lucky.utils.reflect.ClassUtils;

import java.io.IOException;
import java.util.Locale;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/21 下午7:31
 */
public class ComponentAutoScanner extends Scan {

    private final static PathMatchingResourcePatternResolver pm =new PathMatchingResourcePatternResolver();
    private final static int scanRootPackageIndex = ComponentAutoScanner.class.getResource("/").toString().length();
    private final static String CLASS_CONF_TEMP = "classpath:%s/**/*.class";
    private Resource[] classResources;

    public ComponentAutoScanner(Class<?> applicationBootClass) {
        super(applicationBootClass);
        scanner(applicationBootClass);
    }

    private void scanner(Class<?> applicationBootClass){
        String scanRule;
        if(applicationBootClass == null){
            scanRule=String.format(CLASS_CONF_TEMP,"");
        }else{
            String root = applicationBootClass.getPackage().getName().replaceAll("\\.", "/");
            scanRule=String.format(CLASS_CONF_TEMP,root);
        }
        try {
            classResources = pm.getResources(scanRule);
        } catch (IOException e) {
            throw new LuckyIOException(e);
        }
    }

    @Override
    public void autoScan() {
        try{
            for (Resource resource : classResources) {
                String fullClass = resource.getURL().toString();
                fullClass = fullClass.substring(scanRootPackageIndex,fullClass.lastIndexOf("."))
                        .replaceAll("/",".");
                load(ClassUtils.getClass(fullClass));
            }
        }catch (IOException e){
            throw new LuckyIOException(e);
        }


    }

    public ComponentAutoScanner() {
        super(null);
        scanner(null);
    }
}
