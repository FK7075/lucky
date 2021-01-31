package com.lucky.cloud.server.controller;

import com.lucky.cloud.server.conf.LuckyCloudServerConfig;
import com.lucky.cloud.server.core.HttpServer;
import com.lucky.cloud.server.core.Server;
import com.lucky.cloud.server.core.ServerManagement;
import com.lucky.framework.annotation.Autowired;
import com.lucky.utils.reflect.Param;
import com.lucky.web.annotation.*;
import com.lucky.web.controller.LuckyController;
import com.lucky.web.core.BodyObject;
import com.lucky.web.enums.Rest;
import com.lucky.web.webfile.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 14:23
 */
@Controller(id = "LUCKY_CLOUD_SERVER_CONTROLLER")
public class LuckyCloudHttpServerController extends LuckyController {

    private  static ServerManagement manage=new ServerManagement();
    @Autowired
    private ServerWorkCheck job;

    @ResponseBody(Rest.TXT)
    @GetMapping("/lucky/workState")
    public String workState(){
        return "UP";
    }

    /**
     * 注册一个服务
     * @param serverName 服务名
     * @param port 服务对外的端口
     * @param agreement 协议
     */
    @RequestMapping("lucky")
    public void register(@Param("serverName") String serverName,
                         @Param("port") Integer port,
                         @Param("agreement") String agreement){
        String ip=model.getIpAddr();
        Server server=new HttpServer(serverName,ip,port,agreement);
        manage.register(server);
    }

    /**
     * 注销一个服务
     * @param serverName 服务名
     * @param port 服务对外的端口
     * @param agreement 协议
     */
    @RequestMapping("lucky/logout")
    public void logout(@Param("serverName") String serverName,
                       @Param("port") Integer port,
                       @Param("agreement") String agreement){
        String ip=model.getIpAddr();
        Server server=new HttpServer(serverName,ip,port,agreement);
        manage.register(server);
    }

    @ResponseBody(Rest.TXT)
    @RequestMapping("lucky/serverArea")
    public String getServerArea(@Param("serverName") String serverName){
        Server server = manage.getServer(serverName);
        return server.getDomain();
    }

    @ResponseBody
    @RequestMapping("lucky/servers")
    public Map<String, List<Server>> getAllServers(){
        return ServerManagement.getServerPool();
    }

    /**
     * 使用服务名的方式访问注册中心中的服务
     * @param serverName 服务名
     * @return
     * @throws Exception
     */
    @ResponseBody(Rest.TXT)
    @RequestMapping("{serverName}/**")
    public String request(@Param("serverName") String serverName) throws Exception {
        Server server = manage.getServer(serverName);
        String resource=model.getUri().substring(serverName.length()+1);
        Map<String, Object> params = new HashMap<>();
        Map<String, String[]> parameterMap = model.getParameterMap();
        BodyObject bodyObject = model.getBodyObject();
        if(bodyObject!=null){
            params.put("REQUEST_BODY",bodyObject);
        }
        for(Map.Entry<String,String[]> entry:parameterMap.entrySet()){
            String key = entry.getKey();
            String[] value = entry.getValue();
            if (value.length == 1) {
                params.put(key, value[0]);
            } else {
                params.put(key, Arrays.toString(value));
            }
        }
        if (!model.getUploadFileMap().isEmpty()) {
            for (Map.Entry<String, File[]> e : model.getUploadFileMap().entrySet()) {
                params.put(e.getKey(), e.getValue());
            }
        }
        if(!model.getMultipartFileMap().isEmpty()){
            for (Map.Entry<String, MultipartFile[]> e : model.getMultipartFileMap().entrySet()){
                params.put(e.getKey(), e.getValue());
            }
        }
        return (String)server.call(resource,params,model.getRequestMethod());
    }

    @InitRun
    public void check(){
        LuckyCloudServerConfig server=LuckyCloudServerConfig.getLuckyCloudServerConfig();
        job.check(server.getDetectionInterval());;
    }

    @InitRun(4)
    public void registerYourself(){
        manage.registerYourself();
    }
}
