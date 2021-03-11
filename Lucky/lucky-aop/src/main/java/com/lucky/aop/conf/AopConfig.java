package com.lucky.aop.conf;

import com.lucky.aop.aspectj.AspectJAopExecutionChecker;
import com.lucky.aop.core.AopExecutionChecker;
import com.lucky.utils.reflect.ClassUtils;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/10 0010 14:33
 */
public class AopConfig {

    private static AopConfig aopConfig;

    private Class<? extends AopExecutionChecker> aopExecutionCheckerClass;

    public AopExecutionChecker getAopExecutionChecker() {
        return ClassUtils.newObject(aopExecutionCheckerClass);
    }

    public void setAopExecutionChecker(Class<? extends AopExecutionChecker> aopExecutionCheckerClass) {
        this.aopExecutionCheckerClass = aopExecutionCheckerClass;
    }

    private AopConfig(){

    }

    public static AopConfig defaultAopConfig(){
        if(aopConfig==null){
            aopConfig=new AopConfig();
            aopConfig.setAopExecutionChecker(AspectJAopExecutionChecker.class);
        }
        return aopConfig;
    }
}
