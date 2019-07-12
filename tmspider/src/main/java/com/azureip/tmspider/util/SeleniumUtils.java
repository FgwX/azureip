package com.azureip.tmspider.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumUtils {

    /**
     * 初始化浏览器
     */
    public static WebDriver initBrowser(boolean useChrome, Long loadTimeOut) {
        WebDriver driver;
        if (useChrome) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--user-data-dir=C:/Users/LewisZhang/AppData/Local/Google/Chrome/User Data");
            // driver = new ChromeDriver(options);
            driver = new ChromeDriver();
            driver.manage().window().setSize(new Dimension(1002, 538));
        } else {
            // FirefoxOptions options = new FirefoxOptions();
            // options.addArguments("-safe-mode");
            // options.addArguments("-headless");
            // FirefoxProfile profile = new ProfilesIni().getProfile("default");
            // options.setProfile(profile);
            // driver = new FirefoxDriver(options);
            driver = new FirefoxDriver();
            driver.manage().window().setSize(new Dimension(1014, 619));
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
     */
    public static void closeAllButQueryPage(WebDriver driver) {
        // 获取所有标签页的句柄，进行遍历
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (!isQueryPage(driver.getTitle())) {
                driver.close();
            }
        }
    }

    private static boolean isQueryPage(String title) {
        // return StringUtils.isBlank(title) || (!"商标状态检索".equals(title) && !"商标检索结果".equals(title) && !"商标详细内容".equals(title));
        return StringUtils.isNotBlank(title) && "商标状态检索".equals(title);
    }
}
