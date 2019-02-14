package com.azureip.tmspider.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 配置文件动态加载任务
 */
@Component
public class ConfigurationDynamicLoadJob {
    private static final Logger LOG = LogManager.getLogger(ConfigurationDynamicLoadJob.class);
    private static Properties customProps;
    private static final String configFileName = "test.properties";
    private static long lastModified = 0L;

    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() {

        // 判断文件是否被修改
        if (hasModified()) {
        }
    }

    private boolean hasModified() {
        return false;
    }

    private void initLoad() {

    }
}
