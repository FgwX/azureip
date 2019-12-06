package com.azureip.ipspider.job;

import com.azureip.ipspider.service.ProxyIPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

/**
 * 代理IP相关任务
 * @author LewisZhang
 * 5th.Dec.2019
 */
@Component
public class ProxyIPFetchJobs {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPFetchJobs.class);
    @Autowired
    private ProxyIPService proxyIPService;

    // @Scheduled(cron = "0 30 0 * * ?")
    public void fetchXiCiProxyIPJob() throws IOException, ParseException {
        proxyIPService.fetchXiCiProxyIP();
    }

    public void fetch66ProxyIPJob() throws IOException {
        proxyIPService.fetch66ProxyIP();
    }

    public void fetch89ProxyIPJob() throws IOException {
        proxyIPService.fetch89ProxyIP();
    }
}
