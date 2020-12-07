package com.lucky.web.beanfactory;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.web.servlet.LuckyDispatcherServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/7 0007 11:35
 */
public class ServletBeanFactory extends IOCBeanFactory {

    @Override
    public List<Module> createBean() {
        List<Module> modules=new ArrayList<>(1);
        modules.add(new Module("LuckyDispatcherServlet","servlet",new LuckyDispatcherServlet()));
        return modules;
    }
}
