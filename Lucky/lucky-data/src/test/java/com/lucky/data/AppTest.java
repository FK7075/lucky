package com.lucky.data;

import static org.junit.Assert.assertTrue;

import com.lucky.data.pojo.User;
import com.lucky.framework.annotation.Autowired;
import com.lucky.framework.annotation.LuckyApplicationTest;
import com.lucky.framework.junit.LuckyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
@RunWith(LuckyRunner.class)
public class AppTest {

    @Autowired
    User user;
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        System.out.println(user);
    }
}
