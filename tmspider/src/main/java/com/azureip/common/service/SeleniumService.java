package com.azureip.common.service;

import com.azureip.common.exception.ProxyIPBlockedException;
import com.azureip.common.util.SeleniumUtils;
import com.azureip.ipspider.model.ProxyIP;
import com.azureip.ipspider.service.ProxyIPProvider;
import com.azureip.tmspider.constant.TMSConstant;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SeleniumService {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumService.class);
    @Autowired
    private ProxyIPProvider proxyIPProvider;

    public WebDriver initStatusQueryPage(WebDriver driver, String type, ProxyIP proxy, Integer timeout) {
        if (driver != null) {
            SeleniumUtils.quitBrowser(driver);
            driver = null;
        }
        StringBuilder host = new StringBuilder();
        if (proxy != null) {
            host.append(proxy.getIp()).append(":").append(proxy.getPort());
            LOG.info("使用代理[{}]初始化浏览器...", host.toString());
        }
        while (driver == null) {
            try {
                driver = initStatusQueryPage(type, host.toString(), timeout);
            } catch (ProxyIPBlockedException e) {
                LOG.warn("代理IP被屏蔽，更换IP...");
                ProxyIP nextProxy = proxyIPProvider.nextProxy();
                host.delete(0, host.length());
                host.append(nextProxy.getIp()).append(":").append(nextProxy.getPort());
            }
        }
        return driver;
    }

    // 初始化查询页面
    private WebDriver initStatusQueryPage(String type, String host, Integer timeout) {
        LOG.warn("初始化浏览器...");
        WebDriver driver = SeleniumUtils.initBrowser(type, host, timeout);
        int retryTimes = 0;
        // 打开检索系统主页
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            try {
                driver.get(TMSConstant.STATUS_DOMAIN);
                statusQueryEle = new WebDriverWait(driver, 10, 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        // 判断IP是否被拦截
                        if (SeleniumUtils.blockedByHost(driver.getPageSource())) {
                            throw new ProxyIPBlockedException();
                        }
                        // 选择商标状态查询
                        return driver.findElement(By.xpath("//*[@id='txnS03']"));
                    }
                });
            } catch (TimeoutException e) {
                LOG.error("重新打开状态查询页面...");
            }
            if (retryTimes++ >= 5) {
                LOG.error("打开检索系统主页超时！");
                SeleniumUtils.quitBrowser(driver);
                return null;
            }
        }
        statusQueryEle.click();
        // 等待页面加载完成（通过查询按钮判断页面是否加载完成）
        try {
            new WebDriverWait(driver, 12, 500).until(new ExpectedCondition<WebElement>() {
                @NullableDecl
                @Override
                public WebElement apply(WebDriver driver) {
                    // 判断IP是否被拦截
                    if (SeleniumUtils.blockedByHost(driver.getPageSource())) {
                        throw new ProxyIPBlockedException();
                    }
                    return driver.findElement(By.id("_searchButton"));
                }
            });
        } catch (TimeoutException e) {
            LOG.error("跳转到“状态查询”超时！");
            SeleniumUtils.quitBrowser(driver);
            return null;
        }
        return driver;
    }
}
