package com.lucky.quartz.job;


import com.lucky.quartz.TargetJobRun;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.lucky.quartz.constant.Constant.LUCKY_JOB_KEY;

public class LuckyJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobRunBeanId = context.getJobDetail().getJobDataMap().getString(LUCKY_JOB_KEY);
        TargetJobRun targetJobRun = JobMap.get(jobRunBeanId);
        targetJobRun.run();
    }

}
