package com.azureip.ipspider.task;

import com.azureip.ipspider.mapper.ProxyIPMapper;
import com.azureip.ipspider.model.ProxyIP;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Component
public class ProxyIPVerifyTask {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPVerifyTask.class);
    @Autowired(required = false)
    private ProxyIPMapper proxyIPMapper;
    private static final String BDAZ_URL = "https://www.baidu.com/s?ie=utf-8&tn=baidu&wd=%E7%9F%B3%E5%AE%B6%E5%BA%84%E5%AE%89%E6%B3%BD%E7%9F%A5%E8%AF%86%E4%BA%A7%E6%9D%83";
    private static final String AZIP_NAME = "石家庄安泽知识产权服务有限公司";
    private static final String AZIP_URL = "http://www.azure-ip.com";
    private static final String AZIP_GABA = "13012702000121";

    @Async("tmAsyncExecutor")
    public void verifyProxies(ArrayList<ProxyIP> proxies) {
        String thread = Thread.currentThread().getName();
        proxies.forEach(proxy -> {
            LOG.debug(thread + "验证前：" + proxy.toString());
            boolean available = false;
            int httpsResult;
            int httpResult;
            switch (proxy.getType()) {
                case 0:
                    httpsResult = getConnectResult(proxy, BDAZ_URL, AZIP_NAME, 1);
                    httpResult = getConnectResult(proxy, AZIP_URL, AZIP_GABA, 1);
                    if (httpsResult > 0) {
                        proxy.setType(2);
                        available = true;
                        proxy.setInvalidTimes(0);
                    } else if (httpResult > 0) {
                        proxy.setType(1);
                        available = true;
                        proxy.setInvalidTimes(0);
                    } else {
                        proxy.setInvalidTimes(proxy.getInvalidTimes() == null ? 1 : (proxy.getInvalidTimes() + 1));
                    }
                    break;
                case 1:
                    httpResult = getConnectResult(proxy, AZIP_URL, AZIP_GABA, 1);
                    if (httpResult > 0) {
                        available = true;
                        proxy.setInvalidTimes(0);
                    } else {
                        proxy.setInvalidTimes(proxy.getInvalidTimes() == null ? 1 : (proxy.getInvalidTimes() + 1));
                    }
                    break;
                case 2:
                    httpsResult = getConnectResult(proxy, BDAZ_URL, AZIP_NAME, 1);
                    if (httpsResult > 0) {
                        available = true;
                        proxy.setInvalidTimes(0);
                    } else {
                        proxy.setInvalidTimes(proxy.getInvalidTimes() == null ? 1 : (proxy.getInvalidTimes() + 1));
                    }
                    break;
                default:
                    proxy.setDiscarded(true);
                    break;
            }
            proxy.setAvailable(available);
            proxy.setVerifyTime(new Date());
            proxy.setDiscarded(proxy.getInvalidTimes() >= 3);
            LOG.debug(thread + "验证后：" + proxy.toString());
            proxyIPMapper.updateProxyIP(proxy);
        });
    }

    private int getConnectResult(ProxyIP proxy, String url, String key, int times) {
        int result = 0;
        for (int i = 0; i < times; i++) {
            try {
                Connection.Response resp = Jsoup.connect(url).proxy(proxy.getIp(), proxy.getPort()).timeout(5000).execute();
                if (resp.statusCode() == HttpStatus.SC_OK && resp.body().contains(key)) {
                    result++;
                }
            } catch (Exception e) {
                // LOG.error("验证" + proxy.getIp() + ":" + proxy.getPort() + "异常[" + e.getClass().getName() + "]: " + e.getMessage());
            }
        }
        return result;
    }
}
