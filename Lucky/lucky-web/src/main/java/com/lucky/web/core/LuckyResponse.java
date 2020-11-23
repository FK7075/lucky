package com.lucky.web.core;

import com.lucky.web.enums.Rest;
import com.lucky.web.mapping.Mapping;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

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

    default void jump(Model model, Object invoke, Mapping mapping, String prefix, String suffix) throws IOException {
        Rest rest=mapping.getRest();
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
                if (String.class.isAssignableFrom(invoke.getClass())) {
                    toPage(model, invoke.toString(), prefix,suffix);
                } else {
                    RuntimeException e = new RuntimeException("返回值类型错误，无法完成转发和重定向操作!合法的返回值类型为String，错误位置：" + mapping.getMapping());
                    model.error(e,"500");
                }
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
        if (info.contains("@p:")) {//重定向到页面
            info = info.substring(3);
            topage = model.getRequest().getContextPath() + prefix + info + suffix;
            topage = topage.replaceAll(" ", "");
            redirect(model,topage);
        } else if (info.contains("@f:")) {//转发到本Controller的某个方法
            info = info.substring(3);
            forward(model,info);
        } else if (info.contains("@r:")) {//重定向到本Controller的某个方法
            info = info.substring(3);
            redirect(model,info);
        } else {//转发到页面
            topage = prefix + info + suffix;
            topage = topage.replaceAll(" ", "");
            forward(model,topage);
        }
    }

}
