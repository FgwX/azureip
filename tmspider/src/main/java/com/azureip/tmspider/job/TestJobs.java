package com.azureip.tmspider.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestJobs {
    private static final Logger LOG = LogManager.getLogger(TestJobs.class);
    // private volatile boolean canExecute = true;

    // @Async("tmAsyncExecutor")
    // @Scheduled(cron = "0/3 * * * * ?")
    public synchronized void job1() {
        // if (canExecute) {
        //     canExecute = false;
            LOG.warn(Thread.currentThread().getName() + " -> Job1: started...");
            final long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start) < 1500) {
            }
            LOG.warn(Thread.currentThread().getName() + " -> Job1: ended!");
        //     canExecute = true;
        // }
    }

    // @Async("tmAsyncExecutor")
    // @Scheduled(cron = "0/3 * * * * ?")
    public synchronized void job2() {
        // if (canExecute) {
        //     canExecute = false;
            LOG.warn(Thread.currentThread().getName() + " -> Job2: started...");
            final long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start) < 1500) {
            }
            LOG.warn(Thread.currentThread().getName() + " -> Job2: ended!");
        //     canExecute = true;
        // }
    }
}
