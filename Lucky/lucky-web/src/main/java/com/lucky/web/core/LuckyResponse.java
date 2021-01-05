package com.lucky.web.core;

import com.lucky.web.enums.Rest;
import com.lucky.web.mapping.UrlMapping;

import java.io.IOException;

/**
 * 将结果响应给客户端
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/23 下午11:02
 */
public interface LuckyResponse {

    /**
     * 转发
     * @param model 当前请求的Model对象
     */
    void forward(Model model,String url);

    /**
     * 重定向
     * @param model 当前请求的Model对象
     */
    void redirect(Model model,String url);

    default void jump(Model model, Object invoke, Rest rest, String prefix, String suffix) throws IOException {
        if (invoke != null) {
            if (rest == Rest.JSON) {
                model.writerJson(invoke);
                return;
            }
            if (rest == Rest.XML) {
                model.writerXml(invoke);
                return;
            }

            if (rest == Rest.TXT) {
                model.writer(invoke.toString());
                return;
            }
            if (rest == Rest.NO) {
                toPage(model, invoke.toString(), prefix,suffix);
            }
        }
    }

    /**
     * 响应当前请求
     * @param model 当前请求的Model对象
     * @param info 响应的目标
     * @param prefix URL前缀
     * @param suffix URL后缀
     */
    default void toPage(Model model, String info, String prefix, String suffix) {
        String topage = "";
        if (info.startsWith("@p:")) {//重定向到页面
            info = info.substring(3);
            topage = model.getRequest().getContextPath() + prefix + info + suffix;
            topage = topage.replaceAll(" ", "");
            redirect(model,topage);
        } else if (info.startsWith("@f:")) {//转发到本Controller的某个方法
            info = info.substring(3);
            forward(model,info);
        } else if (info.startsWith("@r:")) {//重定向到本Controller的某个方法
            info = info.substring(3);
            redirect(model,info);
        } else {//转发到页面
            topage = prefix + info + suffix;
            topage = topage.replaceAll(" ", "");
            forward(model,topage);
        }
    }

}
