package com.lucky.cloud.client.core;

import com.lucky.cloud.client.conf.LuckyCloudClientConfig;
import com.lucky.utils.base.Assert;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.httpclient.HttpClientCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.lucky.web.httpclient.HttpProxyUtils.call;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午4:41
 */
public class ServiceCall {

    private static final Logger log = LoggerFactory.getLogger("c.l.c.c.c.ServiceCall");
    private static final LuckyCloudClientConfig client=LuckyCloudClientConfig.getLuckyCloudClientConfig();

    private static final String FAILED = "FAILED";
    private static final String SUCCESS = "SUCCESS";

    /**
     * 注册一个服务
     */
    public static void registered(){
        client.check();
        Map<String, String> zones = client.getZones();
        for(Map.Entry<String,String> zone:zones.entrySet()){
            try {
                String start = HttpClientCall.getCall(zone.getValue(), getParamMap());
                if(SUCCESS.equals(start)){
                    log.info("Service `{}` has been successfully registered to {}",client.getName(),zone.getValue());
                } else {
                    throw new ServiceRegistrationException(zone.getKey(),zone.getValue());
                }
            }catch (Exception e){
                throw new ServiceRegistrationException(zone.getKey(),zone.getValue(),e);
            }
        }
    }

    /**
     * 注册服务时需要的参数列表
     * @return 参数列表
     */
    private static Map<String,Object> getParamMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("serverName", client.getName());
        params.put("port", client.getPort());
        params.put("agreement", client.getAgreement());
        params.put("loginPassword",client.getPassword());
        return params;
    }

    /**
     * 从注册中心获取一个服务的域[http://ip:port]
     * @param registry 注册中心名
     * @param serverName 服务名
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static String getServerArea(String registry,String serverName) throws IOException, URISyntaxException {
        //http://ip:port/lucky
        String registryArea = client.getZones().get(registry);
        Assert.notNull(registryArea,"未知的注册中心`"+registry+"`！");
        String serverAreaURL=registryArea+"/serverArea";
        Map<String,Object> serverAreaParamMap=new HashMap<>(1);
        serverAreaParamMap.put("serverName",serverName);
        return HttpClientCall.call(serverAreaURL, RequestMethod.GET,serverAreaParamMap);
    }

    /**
     * 获取注册中心的域[http://ip:port]
     * @param registry 注册中心名
     * @return
     */
    private static String getRegistryArea(String registry){
        String registryArea = client.getZones().get(registry);
        Assert.notNull(registryArea,"未知的注册中心`"+registry+"`！");
        return registryArea.substring(0,registryArea.length()-6);
    }

    /**
     * 获取API的地址
     * @param area 域[http://ip:port]
     * @param resource 资源[/book/getBook]
     * @return
     */
    private static String getApiURL(String area,String resource){
        resource=resource.startsWith("/")?resource:"/"+resource;
        return area+resource;
    }

    /**
     * API调用，通过注册中心转发
     * @param registry 注册中心名
     * @param serverName 服务名
     * @param resource 资源名
     * @param param 参数列表
     * @param method 请求使用的方法[GET/POST/DELETE/PUT/...]
     * @param callType 请求类型[文件请求/字符串请求/byte[]请求/InputStream请求]
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Object callByRegistry(String registry,String serverName,
                                        String resource, Map<String,Object> param,
                                        RequestMethod method,int callType ) throws IOException, URISyntaxException {
        String registryArea = getRegistryArea(registry);
        String apiURL = getApiURL(registryArea+"/"+serverName,resource);
        return call(apiURL,method,param,callType);
    }


    /**
     * API调用，从注册中心中得到服务的具体地址后，直接想服务发送请求
     * @param registry 注册中心名
     * @param serverName 服务名
     * @param resource 资源名
     * @param param 参数列表
     * @param method 请求使用的方法[GET/POST/DELETE/PUT/...]
     * @param callType 请求类型[文件请求/字符串请求/byte[]请求/InputStream请求]
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Object callByServer(String registry,String serverName,
                               String resource, Map<String,Object> param,
                               RequestMethod method,int callType) throws IOException, URISyntaxException {
        String apiURL = getApiURL(getServerArea(registry, serverName), resource);
        return call(apiURL,method,param,callType);
    }
}
