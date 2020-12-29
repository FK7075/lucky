package com.lucky.web.management.conf;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;
import com.lucky.thymeleaf.conf.ThymeleafConfig;
import com.lucky.web.conf.WebConfig;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/29 上午12:34
 */
@Configuration
public class ManagementConfigBeans {

    @Bean
    public void webConfig(){
        WebConfig webConfig = WebConfig.defaultWebConfig();
        webConfig.setOpenStaticResourceManage(true);
    }

    @Bean
    public void th(){
        ThymeleafConfig thymeleafConfig = ThymeleafConfig.defaultThymeleafConfig();
        thymeleafConfig.setEnabled(true);
        thymeleafConfig.setPrefix("classpath:/templates/webmanagement/");
        thymeleafConfig.setSuffix(".html");
    }
}
