package com.lucky.framework;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;
import com.lucky.framework.container.Module;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 13:11
 */
@Configuration
public class ConfigBean {

//    @Bean
    public TestApp module(){
        return new TestApp();
    }
}
