package com.azureip.ipspider.service;

import com.azureip.ipspider.mapper.ProxyIPMapper;
import com.azureip.ipspider.model.ProxyIP;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPVerifyService.class);
    @Autowired(required = false)
    private ProxyIPMapper proxyIPMapper;

    private static final String BDAZ_URL = "https://www.baidu.com/s?ie=utf-8&tn=baidu&wd=%E7%9F%B3%E5%AE%B6%E5%BA%84%E5%AE%89%E6%B3%BD%E7%9F%A5%E8%AF%86%E4%BA%A7%E6%9D%83";
    private static final String AZIP_NAME = "石家庄安泽知识产权服务有限公司";
    private static final String AZIP_URL = "http://www.azure-ip.com";

    public void verify() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -4);
        Date deadline = calendar.getTime();
        List<ProxyIP> pendingProxies = proxyIPMapper.selectUnverifiedProxies(deadline, 100);
        System.out.println("pendingProxies size is " + pendingProxies.size());
        pendingProxies.forEach(proxy -> {
            System.out.println("Verification begin[" + proxy.getIp() + ":" + proxy.getPort() + "]...");
            try {
                Connection.Response response = Jsoup.connect(AZIP_URL).proxy(proxy.getIp(), proxy.getPort()).timeout(5000).execute();
                System.out.println("Response status code is " + response.statusCode());
                if (response.statusCode() == HttpStatus.SC_OK) {
                    Document page = response.parse();
                    boolean ok = page.text().contains("13012702000121");
                    if (ok) {
                        System.out.println("连接安泽主页成功");
                        LOG.info("连接安泽主页成功");
                    } else {
                        System.out.println("连接安泽主页未完成");
                        LOG.info("连接安泽主页未完成");
                    }
                } else {
                    System.err.println("连接安泽主页失败");
                    LOG.warn("连接安泽主页失败");
                }
            } catch (IOException e) {
                System.err.println("异常：[" + e.getClass().getName() + "]" + e.getMessage());
                LOG.error("原因：" + e.getCause());
            }
        });
    }
}
