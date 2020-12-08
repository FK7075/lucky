package com.lucky.quartz.test;

import com.lucky.quartz.proxy.QuartzProxy;
import org.quartz.SchedulerException;

public class JobTestMain {

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        MyJob job = QuartzProxy.getProxy(MyJob.class);
        job.showTime("K");
////        job.ttt();
//        Thread.sleep(1000*5);
//        job.time(3*1000L,10,"TEST-2");
//        Thread.sleep(1000*3);
//        job.time(3*1000L,6,"TEST-3");
        job.ttt();
    }
}
