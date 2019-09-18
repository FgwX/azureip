package com.azureip.tmspider.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumUtils {
    private static final List<String> UA_LIST;
    static {
        UA_LIST = new ArrayList<>();
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:55.0) Gecko/20100101 Firefox/55.0");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36 OPR/47.0.2631.80");
        UA_LIST.add("Mozilla/5.0 (iPad; CPU OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
        UA_LIST.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
        UA_LIST.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        UA_LIST.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
        UA_LIST.add("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
    }

    /**
     * 初始化浏览器
     */
    public static WebDriver initBrowser(boolean useChrome, Long loadTimeOut) {
        WebDriver driver;
        if (useChrome) {
            // ChromeOptions options = new ChromeOptions();
            // options.addArguments("--user-data-dir=C:/Users/LewisZhang/AppData/Local/Google/Chrome/User Data");
            // options.addArguments("--no-infobars");
            // driver = new ChromeDriver(options);
            driver = new ChromeDriver();
            driver.manage().window().setSize(new Dimension(1000, 600));
        } else {
            FirefoxOptions options = new FirefoxOptions();
            // options.addArguments("-safe-mode");
            // options.addArguments("-headless");
            // FirefoxProfile profile = new ProfilesIni().getProfile("default");
            // options.setProfile(profile);
            // driver = new FirefoxDriver(options);
            driver = new FirefoxDriver();
            driver.manage().window().setSize(new Dimension(1000, 600));
        }
        Objects.requireNonNull(driver).manage().window().setPosition(new Point(0, 0));

        // driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        if (loadTimeOut != null && loadTimeOut > 0) {
            driver.manage().timeouts().pageLoadTimeout(loadTimeOut, TimeUnit.MILLISECONDS);
        }
        // driver.manage().timeouts().setScriptTimeout(500, TimeUnit.MILLISECONDS);
        return driver;
    }

    /**
     * 初始化无窗口浏览器（HtmlUnitDriver）
     */
    public static HtmlUnitDriver initHeadlessBrowser(Long loadTimeOut) {
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        Objects.requireNonNull(driver).manage().window().setPosition(new Point(0, 0));
        if (loadTimeOut != null && loadTimeOut > 0) {
            driver.manage().timeouts().pageLoadTimeout(loadTimeOut, TimeUnit.MILLISECONDS);
        }
        return driver;
    }

    /**
     * 关闭所有窗口
     */
    public static void quitBrowser(WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            Set<String> handles = driver.getWindowHandles();
            for (String handle : handles) {
                driver.switchTo().window(handle).close();
            }
            driver.quit();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 通过窗口标题切换窗口
     */
    public static void switchByTitle(WebDriver driver, String targetTitle) {
        // 目标窗口如果是当前窗口，直接返回
        if (targetTitle.equals(driver.getTitle())) {
            return;
        }
        // 获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        // 获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        for (String handle : handles) {
            // 略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            // 切换并检查其Title是否和目标窗口的Title是否相同
            driver.switchTo().window(handle);
            if (targetTitle.equals(driver.getTitle())) {
                return;
            }
        }
    }

    /**
     * 通过句柄切换窗口
     */
    public static void switchByHandle(WebDriver driver, String targetHandle) {
        // 获取所有的窗口句柄
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            // 切换并检查其title是否和目标窗口的title是否相同，是则返回true，否则继续
            if (window.equals(targetHandle)) {
                driver.switchTo().window(window);
                return;
            }
        }
    }

    /**
     * 关闭所有标签页，只保留商标状态检索页
     * 已废弃，因为driver.close()方法导致窗口定位切换问题（NoSuchWindowException: Browsing context has been discarded）
     */
    @Deprecated
    public static void closeAllButQueryPage(WebDriver driver) {
        String queryHandle = null;
        // 获取所有标签页的句柄，进行遍历
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (isQueryPage(driver.getTitle())) {
                queryHandle = driver.getTitle();
            } else {
                driver.close();
            }
        }
        if (StringUtils.isNotBlank(queryHandle)) {
            driver.switchTo().window(queryHandle);
        }
    }

    private static boolean isQueryPage(String title) {
        // return StringUtils.isBlank(title) || (!"商标状态检索".equals(title) && !"商标检索结果".equals(title) && !"商标详细内容".equals(title));
        return StringUtils.isNotBlank(title) && "商标状态检索".equals(title);
    }
}
