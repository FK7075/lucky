package com.lucky.cloud.client.controller;

import com.lucky.cloud.client.conf.LuckyCloudClientConfig;
import com.lucky.cloud.client.core.ServiceCall;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.GetMapping;
import com.lucky.web.annotation.InitRun;
import com.lucky.web.annotation.ResponseBody;
import com.lucky.web.controller.LuckyController;
import com.lucky.web.enums.Rest;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午5:59
 */
@Controller(id="LUCKY_CLOUD_CLIENT_CONTROLLER")
public class LuckyCloudHttpClientController extends LuckyController {


    @ResponseBody(Rest.TXT)
    @GetMapping("/lucky/workState")
    public String workState(){
        return "UP";
    }

    @InitRun(1)
    public void registered(){
        LuckyCloudClientConfig clientConfig=LuckyCloudClientConfig.getLuckyCloudClientConfig();
        ServiceCall.registered();
    }


}
