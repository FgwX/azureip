package com.azureip.common.util;

import com.azureip.common.constant.Constant;
import com.azureip.common.exception.ProxyIPBlockedException;
import com.azureip.tmspider.service.RegistrationService;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumUtils.class);
    // private static final List<String> UA_LIST;

    static {
        String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        System.setProperty("webdriver.chrome.driver", projectBase + "drivers/chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", projectBase + "drivers/geckodriver.exe");
        /*UA_LIST = new ArrayList<>();
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
        UA_LIST.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");*/
    }

    /**
     * 初始化浏览器
     */
    public static WebDriver initBrowser(String type, String host, Integer timeOut) {
        WebDriver driver;
        Proxy proxy = null;
        if (StringUtils.isNotEmpty(host)) {
            proxy = new Proxy().setHttpProxy(host).setFtpProxy(host).setSslProxy(host);
        }
        switch (type) {
            case Constant.WEB_DRIVER_CHROME:
                ChromeOptions chromeOption = new ChromeOptions();
                if (proxy != null) {
                    chromeOption.setProxy(proxy);
                }
                chromeOption.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                // options.addArguments("--user-data-dir=C:/Users/LewisZhang/AppData/Local/Google/Chrome/User Data");
                // options.addArguments("--no-infobars");
                driver = new ChromeDriver(chromeOption);
                // driver = new ChromeDriver();
                driver.manage().window().setSize(new Dimension(1000, 600));
                break;
            case Constant.WEB_DRIVER_FIREFOX:
                FirefoxOptions firefoxOption = new FirefoxOptions();
                if (proxy != null) {
                    firefoxOption.setProxy(proxy);
                }
                // options.addArguments("-safe-mode");
                // options.addArguments("-headless");
                // FirefoxProfile profile = new ProfilesIni().getProfile("default");
                // options.setProfile(profile);
                driver = new FirefoxDriver(firefoxOption);
                // driver = new FirefoxDriver();
                driver.manage().window().setSize(new Dimension(1000, 600));
                break;
            default:
                driver = new HtmlUnitDriver();
                break;
        }

        driver.manage().window().setPosition(new Point(0, 0));

        // driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        if (timeOut != null && timeOut > 0) {
            driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
        }
        // driver.manage().timeouts().setScriptTimeout(500, TimeUnit.MILLISECONDS);
        return driver;
    }

    /**
     * 关闭所有窗口
     * @param driver WebDriver
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
            LOG.error("关闭所有窗口异常[" + e.getClass().getName() + "]：" + e.getMessage());
        }
    }

    /**
     * 通过窗口标题切换窗口
     * @param driver      WebDriver
     * @param targetTitle 目标窗口标题
     */
    public static boolean switchByTitle(WebDriver driver, String targetTitle) {
        // 目标窗口如果是当前窗口，直接返回
        if (targetTitle.equals(driver.getTitle())) {
            return true;
        }
        // 获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        // 获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            // 略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            // 切换并检查其Title是否和目标窗口的Title是否相同
            driver.switchTo().window(handle);
            String title = driver.getTitle();
            if ("请继续".equals(title)) {
                throw new ProxyIPBlockedException();
            } else if (targetTitle.equals(title)) {
                return true;
            }
        }
        LOG.error("窗口切换失败：{}窗口不存在！", targetTitle);
        return false;
    }

    /**
     * 通过句柄切换窗口
     * @param driver       WebDriver
     * @param targetHandle 目标窗口句柄
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

    public static boolean blockedByHost(WebDriver driver) {
        Set<String> handles = driver.getWindowHandles();
        boolean blocked = false;
        for (String handle : handles) {
            driver.switchTo().window(handle);
            String pageSource = driver.getPageSource();
            if (StringUtils.isNotEmpty(pageSource) && (pageSource.contains("502 Bad Gateway")
                    || pageSource.contains("该操作已触发系统访问防护规则")
                    || pageSource.contains("请点击图片中的")
                    || pageSource.contains("的符号的序号填写在输入框内")
                    || pageSource.contains("请将图片中汉字读音对应的数字填写在输入框内")
                    || pageSource.contains("请将图片中显示的字母、数字填写在输入框中"))) {
                blocked = true;
                break;
            }
        }
        return blocked;
    }
}
