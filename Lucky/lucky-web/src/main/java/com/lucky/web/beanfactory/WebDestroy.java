package com.lucky.web.beanfactory;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.factory.Destroy;
import com.lucky.web.mapping.UrlMappingCollection;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/6 下午7:42
 */
public class WebDestroy implements Destroy {

    @Override
    public void destroy() {
        AutoScanApplicationContext applicationContext = AutoScanApplicationContext.create();
        if(applicationContext.isIOCId("lucky_UrlMappingCollection")){
            UrlMappingCollection urlMappingCollection = (UrlMappingCollection) applicationContext.getBean("lucky_UrlMappingCollection");
            urlMappingCollection.closeRun();
        }
    }
}
