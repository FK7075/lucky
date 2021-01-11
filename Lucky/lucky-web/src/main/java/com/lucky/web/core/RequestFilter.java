package com.lucky.web.core;

import com.lucky.utils.file.FileUtils;
import com.lucky.utils.file.Resources;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.webfile.StaticResourceManage;
import com.lucky.web.webfile.WebFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 请求过滤器
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 18:23
 */
public abstract class RequestFilter {

    private static final Logger log= LoggerFactory.getLogger(RequestFilter.class);
    private static final String ICO ="/favicon.ico";
    private static byte[] IOC_BYTE;

    /**
     * 过滤处理当前请求，无法处理返回true，可以处理并已经处理返回false
     * @param model 当前请求的Model对象
     * @param webConfig Web配置类
     * @return
     */
    public static boolean filter(Model model, WebConfig webConfig) throws IOException {
        if(!isICO(model, webConfig))
            return false;
        if(!ipIsPass(model, webConfig)){
            return false;
        }
        if(!isStaticResource(model, webConfig)){
            return false;
        }
        if(!staticHander(model, webConfig)){
            return false;
        }
        return true;
    }

    /**
     * favicon.ico请求的处理
     * @param model 当前请求的Model对象
     * @param webConfig Web配置类
     * @return
     * @throws IOException
     */
    public static boolean isICO(Model model, WebConfig webConfig) throws IOException {
        if(ICO.equals(model.getUri())){
            if(IOC_BYTE==null){
                IOC_BYTE= FileUtils.copyToByteArray(Resources.getInputStream(webConfig.getFavicon()));
            }
            model.getResponse().setContentType("image/x-icon");
            WebFileUtils.preview(model, IOC_BYTE,ICO);
            return false;
        }
        return true;
    }

    /**
     * 全局IP配置校验
     * @param model 当前请求的Model对象
     * @param webConfig Web配置类
     * @return
     */
    public static boolean ipIsPass(Model model, WebConfig webConfig) {
        String currIp = model.getIpAddr();
        //全局资源的IP限制
        if (!webConfig.getGlobalResourcesIpRestrict().isEmpty() && !webConfig.getGlobalResourcesIpRestrict().contains(currIp)) {
            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
            log.info("403 : 不合法的请求ip：" + currIp + "该ip地址没有被注册，服务器拒绝响应！");
            return false;
        }
        //指定资源的IP限制
        String path = model.getUri();
        if (!webConfig.getSpecifiResourcesIpRestrict().isEmpty() && (webConfig.getSpecifiResourcesIpRestrict().containsKey(path) && !webConfig.getSpecifiResourcesIpRestrict().get(path).contains(currIp))) {
            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
            log.info("403 : 不合法的请求ip：" + currIp + "该ip地址没有被注册，服务器拒绝响应！");
            return false;
        }
        return true;
    }

    /**
     * 是否可以处理静态资源
     * @param model 当前请求的Model对象
     * @param webConfig Web配置类
     * @return
     */
    public static boolean isStaticResource(Model model, WebConfig webConfig) {
        String uri = model.getUri();
        if (webConfig.isOpenStaticResourceManage()
                && StaticResourceManage.isLegalRequest(webConfig,
                model.getIpAddr(),
                model.getResponse(),
                uri)
        ) {
            try {
                if (StaticResourceManage.resources(model, uri)) {
                    //静态资源处理
                    log.debug("STATIC-REQUEST [静态资源请求]  [" + model.getRequestMethod() + "]  #SR#=> " + uri);
                    StaticResourceManage.response(model, uri);
                    return false;
                } else {
                    model.error("404", "服务器中找不到资源文件 " + uri + "！", "找不到资源 " + uri);
                    return false;
                }
            } catch (Exception e) {
                model.error(e,"500");
                return false;
            }

        }
        return true;
    }

    /**
     * 当前请求是否有静态URL映射配置
     * @param model 当前请求的Model对象
     * @param webConfig Web配置类
     * @return
     */
    public static boolean staticHander(Model model, WebConfig webConfig){
        //扫描并执行配置中的映射
        if(webConfig.getStaticHander().containsKey(model.getUri())){
            String forwardurl = webConfig.getPrefix() + webConfig.getStaticHander().get(model.getUri()) + webConfig.getSuffix();
            model.forward(forwardurl);
            return false;
        }
        return true;
    }
}
