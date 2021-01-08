package com.lucky.mybatis.beanfactory;

import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.container.factory.Destroy;

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
