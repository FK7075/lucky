package com.lucky.data.beanfactory;

import com.lucky.framework.container.factory.Destroy;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 下午11:20
 */
public class LuckyDataDestroy implements Destroy {

    @Override
    public void destroy() {
        LuckyDataSourceManage.destroy();
    }
}
