package com.azureip.tmspider.util;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumUtil {

    /**
     * WebDriver初始化
     */
    public static WebDriver initDriver(boolean useChrome) {
        WebDriver driver = null;
        if (useChrome) {
            // ChromeOptions options = new ChromeOptions();
            // DesiredCapabilities cap = DesiredCapabilities.chrome();
            // cap.setCapability(ChromeOptions.CAPABILITY, options);
            // LoggingPreferences logPref = new LoggingPreferences();
            // logPref.enable(LogType.PERFORMANCE, Level.ALL);
            // cap.setCapability(CapabilityType.LOGGING_PREFS, logPref);
            // options.setCapability(ChromeOptions.CAPABILITY, cap);
            // driver = new ChromeDriver(options);
        } else {
            // DesiredCapabilities cap = DesiredCapabilities.firefox();
            // LoggingPreferences logPref = new LoggingPreferences();
            // logPref.enable(LogType.PERFORMANCE, Level.ALL);
            // cap.setCapability(CapabilityType.LOGGING_PREFS, logPref);
            FirefoxOptions options = new FirefoxOptions();
            FirefoxProfile profile = new ProfilesIni().getProfile("default");
            options.setProfile(profile);
            options.addArguments("-safe-mode");
            driver = new FirefoxDriver(options);
        }

        Objects.requireNonNull(driver).manage().window().setPosition(new Point(0, 0));
        // for Chrome
        // driver.manage().window().setSize(new Dimension(1002,538));
        // for Firefox
        driver.manage().window().setSize(new Dimension(1014, 619));
        // driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(5000, TimeUnit.MILLISECONDS);
        // driver.manage().timeouts().setScriptTimeout(500, TimeUnit.MILLISECONDS);
        return driver;
    }

    /**
     * 关闭所有窗口
     */
    public static void quitBrowser(WebDriver driver) {
        if (driver == null) {
            return;
        }
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle).close();
        }
        // driver.quit();
    }

    /**
     * 通过窗口标题切换窗口
     */
    public static void switchByTitle(WebDriver driver, String targetTitle) {
        // LOG.warn("[切换窗口] ==> " + targetTitle);
        //获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        //获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        //获取当前窗口的title
        String currentTitle = driver.getTitle();
        if (currentTitle.equals(targetTitle)) {
            return;
        }
        for (String handle : handles) {
            //略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            //切换并检查其Title是否和目标窗口的Title是否相同
            if ((driver.switchTo().window(handle).getTitle()).equals(targetTitle)) {
                return;
            }
        }
    }

    /**
     * 通过句柄切换窗口
     */
    public static void switchByHandle(WebDriver driver, String targetHandle) {
        //获取所有的窗口句柄
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            //切换并检查其title是否和目标窗口的title是否相同，是则返回true，否则继续
            if (window.equals(targetHandle)) {
                driver.switchTo().window(window);
                return;
            }
        }
    }
}
