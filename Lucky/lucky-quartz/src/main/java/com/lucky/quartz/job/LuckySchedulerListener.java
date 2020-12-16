package com.lucky.quartz.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/10 9:52
 */
public class LuckySchedulerListener implements SchedulerListener {

    private static final Logger log= LoggerFactory.getLogger("c.l.quartz.job.LuckySchedulerListener");

    @Override
    public void jobScheduled(Trigger trigger) {

    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {

    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        String key=trigger.getKey().getName();
        if(JobMap.has(key)){
            JobMap.del(key);
            log.info("Timed task \" ["+key+"] \" has been executed!");
        }
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {

    }

    @Override
    public void triggersPaused(String triggerGroup) {

    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {

    }

    @Override
    public void triggersResumed(String triggerGroup) {

    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
    }

    @Override
    public void jobDeleted(JobKey jobKey) {

    }

    @Override
    public void jobPaused(JobKey jobKey) {

    }

    @Override
    public void jobsPaused(String jobGroup) {

    }

    @Override
    public void jobResumed(JobKey jobKey) {

    }

    @Override
    public void jobsResumed(String jobGroup) {

    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {

    }

    @Override
    public void schedulerInStandbyMode() {

    }

    @Override
    public void schedulerStarted() {

    }

    @Override
    public void schedulerStarting() {

    }

    @Override
    public void schedulerShutdown() {

    }

    @Override
    public void schedulerShuttingdown() {

    }

    @Override
    public void schedulingDataCleared() {

    }

}
