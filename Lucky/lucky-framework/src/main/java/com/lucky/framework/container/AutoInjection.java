package com.lucky.framework.container;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/29 上午2:24
 */
public class AutoInjection {

    public AutoInjection(){
        Injection.injection(this,"component");
    }
}
