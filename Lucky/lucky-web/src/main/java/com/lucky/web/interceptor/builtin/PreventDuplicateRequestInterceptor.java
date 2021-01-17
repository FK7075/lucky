package com.lucky.web.interceptor.builtin;

import com.lucky.web.core.Model;
import com.lucky.web.interceptor.HandlerInterceptor;
import com.lucky.web.mapping.UrlMapping;

import java.util.Date;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/17 下午6:33
 */
public class PreventDuplicateRequestInterceptor implements HandlerInterceptor {

    private static final long TIME_DIFFERENCE=1000L;

    @Override
    public boolean preHandle(Model model, UrlMapping handler) throws Exception {
        long currTime = new Date().getTime();
        String handlerId = handler.getId();
        Object timeObj = model.getSessionAttribute(handlerId);
        model.setSessionAttribute(handlerId,currTime);
        if(timeObj==null){
            return true;
        }
        long lastTime= (long) timeObj;
        //同一用户对同一接口的访问时间差小于预先设定的时间差
        if(currTime-lastTime<TIME_DIFFERENCE){
            return false;
        }
        return true;
    }
}
