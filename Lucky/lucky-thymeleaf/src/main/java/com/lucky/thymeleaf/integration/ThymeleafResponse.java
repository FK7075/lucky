package com.lucky.thymeleaf.integration;

import com.lucky.thymeleaf.core.ThymeleafWrite;
import com.lucky.web.core.LuckyResponse;
import com.lucky.web.core.Model;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/21 0021 15:12
 */
public class ThymeleafResponse implements LuckyResponse {

    @Override
    public void forward(Model model, String url) {
        ThymeleafWrite.write(model,url);
    }

    @Override
    public void redirect(Model model, String url) {
        model.redirect(url);
    }
}
