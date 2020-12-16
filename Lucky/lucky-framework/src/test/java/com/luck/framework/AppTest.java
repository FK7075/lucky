package com.luck.framework;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.junit.LuckyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
@RunWith(LuckyRunner.class)
public class AppTest {

    private static final Logger log= LoggerFactory.getLogger(AppTest.class);

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        System.out.println(ConfigUtils.getYamlConfAnalysis().getMap());
    }
}
