package com.azureip.tmspider.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestJobs {
    private static final Logger LOG = LogManager.getLogger(TestJobs.class);

    // @Async("tmTaskScheduler")
    // @Scheduled(cron = "0/5 * * * * ?")
    public void job1() {
        LOG.warn(Thread.currentThread().getName() + " -> Job1: started...");
        final long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 10000) {
        }
        LOG.warn(Thread.currentThread().getName() + " -> Job1: ended!");
    }

    // @Async("tmTaskScheduler")
    // @Scheduled(cron = "2/5 * * * * ?")
    public void job2() {
        LOG.warn(Thread.currentThread().getName() + " -> Job2: started...");
        final long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 10000) {
        }
        LOG.warn(Thread.currentThread().getName() + " -> Job2: ended!");
    }
}
