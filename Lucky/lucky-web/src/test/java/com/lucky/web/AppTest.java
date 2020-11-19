package com.lucky.web;

import static org.junit.Assert.assertTrue;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.annotation.LuckyApplicationTest;
import com.lucky.framework.junit.LuckyRunner;
import com.lucky.web.conf.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
@RunWith(LuckyRunner.class)
@LuckyApplicationTest
public class AppTest{

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        WebConfig web=WebConfig.getWebConfig();
        WebConfig web1=WebConfig.getWebConfig();
        System.out.println(web==web1);
    }
}
