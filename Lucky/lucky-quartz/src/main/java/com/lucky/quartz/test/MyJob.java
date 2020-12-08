package com.lucky.quartz.test;


import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.quartz.annotation.Job;

public class MyJob {

    @Job(cron = "1/2 * * * * ?")
    public void showTime(String k){
        System.out.println("showTime("+k+") || Run==>"+ BaseUtils.time());
    }

    @Job(dyInterval = "interval", dyCount ="counte",onlyLast = true)
    public void time(Long interval,Integer counte,String fk){
        System.out.println("time("+fk+") || Run==>"+ BaseUtils.time());
    }

    @Job(count = 5,interval = 3*1000L)
    public void ttt() throws InterruptedException {
        System.out.println(BaseUtils.time()+" ==> kokokok");
        Thread.sleep(7*1000L);
    }
}
