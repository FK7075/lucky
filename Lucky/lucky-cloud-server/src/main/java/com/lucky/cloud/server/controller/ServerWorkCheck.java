package com.lucky.cloud.server.controller;

import com.lucky.cloud.server.core.ServerManagement;
import com.lucky.quartz.annotation.Job;
import com.lucky.quartz.annotation.QuartzJobs;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/30 下午7:44
 */
@QuartzJobs
public class ServerWorkCheck {

    @Job(dyInterval = "time")
    public void check(Long time){
        ServerManagement.workCheck();
    }
}
