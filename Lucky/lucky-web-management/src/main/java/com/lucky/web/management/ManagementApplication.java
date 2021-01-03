package com.lucky.web.management;

import com.lucky.boot.startup.LuckyApplication;
import com.lucky.framework.annotation.LuckyBootApplication;

/**
 * Hello world!
 *
 */
@LuckyBootApplication
public class ManagementApplication {

    public static void main( String[] args ) {
        LuckyApplication.run(ManagementApplication.class,args);
    }
}
