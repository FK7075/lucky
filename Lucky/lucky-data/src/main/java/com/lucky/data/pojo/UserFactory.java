package com.lucky.data.pojo;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 9:22
 */
@Component
public class UserFactory extends IOCBeanFactory {

    @Override
    public Map<String, Module> replaceBean() {
        Module user=getBean("user");
        System.out.println("原user:"+user.getComponent());
        User newUser=new User(3,"cl","%*#!$");
        user.setComponent(newUser);
        return super.replaceBean();
    }
}
