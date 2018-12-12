package com.azureip.tmspider.controller;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumController {

    private static String tmQueryIndexUrl = "http://wsjs.saic.gov.cn/txnS03.do?y7bRbp=qmM97MmkcZ7bzT69vS0XJn9fZrE71Y_4QcpKAP4YkKW7DTXZ1VwXLzsu.viruyGhQBnGSc4KKOmZp0MWdjQ.aqk7pgjUeZM.HRsDP0ISKIsmGtUrn8leSweH2tIFWZM.RNu.RShwWnL6EPa9zKfuHJ80Ack&c1K5tw0w6_=2pANjaxPYr1juaLLh4XQw7ObiNYQJpkYY3ZBNIMSeIhLd8LRvUJ4sy.VtYq2OUrLoSWXgnjBuy4GPfkAyyOJmLze9ciijHelCLFC8DqNVxaqAGmjDD1pP57637rwfLvHB";

    private static String searchWinTitle = "商标状态检索";
    private static String resultWinTitle = "商标检索结果";
    private static String detailWinTitle = "商标详细内容";

    public static void main(String[] args) {
        String chromeDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\chromedriver.exe";
        String chromeBinDir = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
        String firefoxDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\geckodriver.exe";
        String firefoxBinDir = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                " Chrome/70.0.3538.110 Safari/537.36";

        // 设置系统属性
        // System.setProperty("webdriver.chrome.driver", chromeDriverDir);
        System.setProperty("webdriver.firefox.bin", firefoxBinDir);
        System.setProperty("webdriver.gecko.driver", firefoxDriverDir);

        // 创建Chrome驱动
        /*ChromeOptions options = new ChromeOptions();
        // options.addArguments("user-agent=" + userAgent);
        // options.addArguments("--user-data-dir=C:/Users/lewiszhang/AppData/Local/Google/Chrome/User Data");
        ChromeDriver driver = new ChromeDriver(options);*/

        // 创建FireFox驱动
        // GeckoDriverService geckoDriverService = new GeckoDriverService.Builder()
        //         // .usingFirefoxBinary(new FirefoxBinary(new File(firefoxBinDir)))
        //         // .usingDriverExecutable(new File(firefoxDriverDir))
        //         .build();
        FirefoxDriver driver = new FirefoxDriver();

        // 设置等待方式及时间
        driver.manage().window().setSize(new Dimension(1200, 700));
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);

        // 打开检索系统主页
        driver.get(tmQueryIndexUrl);

        String regNums = "31347083,31348939,31347548";
        driver.findElementByCssSelector("#submitForm>div>div.searchbox>table>tbody>tr>td:nth-child(2)>div>input")
                .sendKeys(regNums.split(",")[1]);
        driver.findElementById("_searchButton").submit();

        // switchWindows(driver, searchWinTitle);

        final String text = driver.findElementByCssSelector("#list_box>table>tbody>tr.ng-scope>td:nth-child(5)>a").getText();
        System.out.println(text);

    }

    // 切换到指定窗口
    public static boolean switchWindows(WebDriver driver, String windowsTitle) {
        //获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        //获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        //获取当前窗口的title
        String currentTitle = driver.getTitle();
        //要切换窗口为当前窗口则直接返回true
        if (currentTitle.equals(windowsTitle)) {
            return true;
        }
        //处理要切换到的窗口非当前窗口的情况
        for (String handle : handles) {
            //略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            //切换并检查其title是否和目标窗口的title是否相同，是则返回true，否则继续
            if ((driver.switchTo().window(handle).getTitle()).equals(windowsTitle)) {
                return true;
            }
        }
        return false;
    }

}
