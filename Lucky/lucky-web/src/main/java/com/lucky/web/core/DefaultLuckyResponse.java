package com.lucky.web.core;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/23 下午11:09
 */
public class DefaultLuckyResponse implements LuckyResponse{


    @Override
    public void forward(Model model, String url) {
        model.forward(url);
    }

    @Override
    public void redirect(Model model, String url) {
        model.redirect(url);
    }
}
