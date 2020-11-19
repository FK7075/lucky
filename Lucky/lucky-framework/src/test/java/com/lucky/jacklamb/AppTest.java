package com.lucky.jacklamb;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.junit.LuckyRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
@RunWith(LuckyRunner.class)
public class AppTest {

    private static final Logger log= LogManager.getLogger(AppTest.class);

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        System.out.println(ConfigUtils.getYamlConfAnalysis().getMap());
    }
}
