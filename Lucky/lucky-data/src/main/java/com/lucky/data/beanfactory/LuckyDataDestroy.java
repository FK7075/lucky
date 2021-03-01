package com.lucky.data.beanfactory;

import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.container.factory.Destroy;

import java.io.IOException;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 下午11:20
 */
public class LuckyDataDestroy implements Destroy {

    @Override
    public void close() throws IOException {
        LuckyDataSourceManage.destroy();
    }
}
