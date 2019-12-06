package com.azureip.ipspider.job;

import com.azureip.ipspider.service.ProxyIPVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 代理IP验证任务
 * @author LewisZhang
 * 6th.Dec.2019
 */
@Component
public class ProxyIPVerifyJob {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPVerifyJob.class);
    @Autowired
    private ProxyIPVerifyService proxyIPVerifyService;
}
