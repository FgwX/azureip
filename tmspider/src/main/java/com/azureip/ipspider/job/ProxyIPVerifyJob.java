package com.azureip.ipspider.job;

import com.azureip.ipspider.service.ProxyIPVerifyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 代理IP验证任务
 * @author LewisZhang
 * 6th.Dec.2019
 */
@Component
public class ProxyIPVerifyJob {
    private static final Logger LOG = LogManager.getLogger(ProxyIPVerifyJob.class);
    @Autowired
    private ProxyIPVerifyService proxyIPVerifyService;

    /**
     * 代理IP验证任务
     */
    // @Scheduled(cron = "0 0/3 * * * ?")
    public void fetchXiCiProxyIPJob() {
        LOG.debug("代理IP验证任务开始执行...");
        proxyIPVerifyService.verifyProxyIP(50, 8);
    }
}
