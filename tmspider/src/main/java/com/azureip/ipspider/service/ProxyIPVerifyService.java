package com.azureip.ipspider.service;

import com.azureip.ipspider.mapper.ProxyIPMapper;
import com.azureip.ipspider.model.ProxyIP;
import com.azureip.ipspider.task.ProxyIPVerifyTask;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 代理IP验证服务类
 * @author LewisZhang
 * 6th.Dec.2019
 */
@Service
public class ProxyIPVerifyService {
    private static final Logger LOG = LogManager.getLogger(ProxyIPVerifyService.class);
    @Autowired(required = false)
    private ProxyIPMapper proxyIPMapper;
    @Autowired
    private ProxyIPVerifyTask proxyIPVerifyTask;

    /**
     * 代理IP验证
     * @param payload 负载
     * @param threads 线程数
     */
    public void verifyProxyIP(Integer payload, Integer threads) {
        // 提取IP
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -6);
        Date deadline = calendar.getTime();
        List<ProxyIP> pendingProxies = proxyIPMapper.selectUnverifiedProxies(deadline, payload * threads);
        LOG.debug("查询到的列表大小为：" + pendingProxies.size());
        List<ProxyIP> tmpList = new ArrayList<>();
        pendingProxies.forEach(p -> {
            tmpList.add(p);
            if (tmpList.size() >= payload) {
                proxyIPVerifyTask.verifyProxies(new ArrayList<>(tmpList));
                tmpList.clear();
            }
        });
        if (!tmpList.isEmpty()) {
            proxyIPVerifyTask.verifyProxies(new ArrayList<>(tmpList));
        }
    }
}
