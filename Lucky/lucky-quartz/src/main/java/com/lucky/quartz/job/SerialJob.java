package com.lucky.quartz.job;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class SerialJob extends LuckyJob {

}
