package com.azureip.tmspider.controller;

import com.azureip.tmspider.util.SpringUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("selenium")
public class SeleniumController {

    private static String searchWinTitle = "商标状态检索";
    private static String resultWinTitle = "商标检索结果";
    private static String detailWinTitle = "商标详细内容";
    private static String rejectionMark = "驳回通知发文";

    private static AtomicBoolean isOperating = new AtomicBoolean(true);

    @GetMapping("test")
    public void test() {
        // 初始化驱动
        String firefoxDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\drivers\\geckodriver.exe";
        String firefoxBinDir = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
        System.setProperty("webdriver.firefox.bin", firefoxBinDir);
        System.setProperty("webdriver.gecko.driver", firefoxDriverDir);
        FirefoxDriver driver = new FirefoxDriver();
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        // 开启单独线程模拟鼠标移动
        Actions action = new Actions(driver);
        SpringUtils.getBean(SeleniumController.class).mouseAnalysing(action);

        driver.get("http://wsjs.saic.gov.cn");
        // 选择商标状态查询
        driver.findElementByCssSelector("body > div.centent > div.left_side > ul > li:nth-child(3) > table").click();
        String regNums = "31347083,31348939,31347548";
        driver.findElementByCssSelector("#submitForm>div>div.searchbox>table>tbody>tr>td:nth-child(2)>div>input").sendKeys(regNums.split(",")[1]);
        driver.findElementById("_searchButton").submit();
        switchWindows(driver, resultWinTitle);
        // wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]/a"),"31348939"));
        WebElement linkElement = null;
        while (linkElement == null) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 10, 2000);
                // 每隔200毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                linkElement = wait.until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver webDriver) {
                        System.out.println("====> applying...");
                        WebElement element = webDriver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]"));
                        if (element != null && "31348939".equals(element.findElement(By.tagName("a")).getText())) {
                            System.out.println("====> apply success");
                            return element;
                        } else {
                            System.out.println("====> apply failed");
                            return null;
                        }
                    }
                });
            } catch (Exception e) {
                switchWindows(driver, searchWinTitle);
                System.out.println("====> redo submit...");
                driver.findElementById("_searchButton").submit();
                switchWindows(driver, resultWinTitle);
            }
        }
        // driver.findElementByXPath("//*[@id='list_box']/table/tbody/tr[2]/td[2]").click();
        // wait(5000);
        linkElement.click();
        switchWindows(driver, detailWinTitle);
        List<WebElement> regFlows = driver.findElementsByCssSelector("body>div.xqboxx>div>ul>li");
        for (WebElement flow : regFlows) {
            WebElement element = flow.findElement(By.cssSelector("table>tbody>tr>td:nth-child(3)>span"));
            System.out.println(element.getText());
        }
    }

    @Async("tmAsyncExecutor")
    public void mouseAnalysing(Actions action) {
        while (isOperating.get()) {
            try {
                action.moveByOffset(1024, 0);
                Thread.sleep(500);
                action.moveByOffset(1024, 512);
                Thread.sleep(500);
                action.moveByOffset(0, 512);
                Thread.sleep(500);
                action.moveByOffset(0, 0);
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        List<String> regNums = new ArrayList<>();
        regNums.add("32612659");
        regNums.add("32613004");
        regNums.add("32613005");

        String chromeDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\drivers\\chromedriver.exe";
        String chromeBinDir = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
        String firefoxDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\drivers\\geckodriver.exe";
        String firefoxBinDir = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
        String userAgentIE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

        // 设置系统属性
        // System.setProperty("webdriver.chrome.driver", chromeDriverDir);
        System.setProperty("webdriver.firefox.bin", firefoxBinDir);
        System.setProperty("webdriver.gecko.driver", firefoxDriverDir);

        // 创建Chrome驱动
        // ChromeOptions options = new ChromeOptions();
        // options.addArguments("user-agent=" + userAgent);
        // options.addArguments("--user-data-dir=C:/Users/lewiszhang/AppData/Local/Google/Chrome/User Data");
        // ChromeDriver driver = new ChromeDriver(options);

        // 创建FireFox驱动
        // GeckoDriverService geckoDriverService = new GeckoDriverService.Builder()
        //         .usingFirefoxBinary(new FirefoxBinary(new File(firefoxBinDir)))
        //         .usingDriverExecutable(new File(firefoxDriverDir)).build();
        FirefoxDriver driver = new FirefoxDriver();
        // driver.manage().addCookie(new Cookie("JSESSIONID","8358404E2F0617FBACFD9BD97CA76C34"));
        // 设置等待方式及时间
        // driver.manage().window().setSize(new Dimension(1200, 700));
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        // driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

        // driver.get("https://www.baidu.com");
        // wait(1000);
        // driver.quit();
        // driver = null;
        // driver = new FirefoxDriver();
        // driver.get("https://www.baidu.com");
        driver.get("https://www.baidu.com");
        wait(1000);
        driver.executeScript("window.open('https://www.sogou.com');");
        wait(1500);
        driver.quit();


        /*// 打开检索系统主页
        int retryTimes = 0;
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            retryTimes++;
            driver.get("http://wsjs.saic.gov.cn");
            try {
                WebDriverWait wait = new WebDriverWait(driver, 5, 500);
                statusQueryEle = wait.until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver driver) {
                        // 选择商标状态查询
                        return ((FirefoxDriver) driver).findElementByCssSelector("body > div.centent > div.left_side > ul > li:nth-child(3) > table");
                    }
                });
            } catch (TimeoutException e) {
                System.out.println("====> redo getting...");
                driver.get("http://wsjs.saic.gov.cn");
            }
            if (retryTimes >= 5) {
                System.out.println("Retried too many times, end operation...");
                return;
            }
        }
        retryTimes = 0;
        statusQueryEle.click();

        WebDriverWait wait = new WebDriverWait(driver, 10, 1000);
        wait.until(new ExpectedCondition<WebElement>() {
            @NullableDecl
            @Override
            public WebElement apply(@NullableDecl WebDriver webDriver) {
                return driver.findElementByCssSelector("#submitForm>div>div.searchbox>table>tbody>tr>td:nth-child(2)>div>input");
            }
        });

        for (String regNum : regNums) {
            WebElement rejectDateEle = queryRejectDateWithFxDriver(driver, regNum);
            if (rejectDateEle != null) {
                System.out.println(regNum + "驳回通知发文日期：" + rejectDateEle.getText());
            } else {
                System.out.println(regNum + "未查询到驳回通知");
            }
        }*/

    }

    // 使用FirefoxDriver通过注册号查询驳回信息
    private static WebElement queryRejectDateWithFxDriver(FirefoxDriver driver, String regNum) {
        // 切换到查询页，输入注册号，进行查询
        switchWindows(driver, searchWinTitle);
        WebElement inputBox = driver.findElementByCssSelector("#submitForm>div>div.searchbox>table>tbody>tr>td:nth-child(2)>div>input");
        inputBox.clear();
        inputBox.sendKeys(regNum);
        WebElement submitBtn = driver.findElementById("_searchButton");
        submitBtn.submit();

        // 切换到结果页，等待结果加载完成后，点击详情页链接
        switchWindows(driver, resultWinTitle);
        WebElement linkElement = null;
        while (linkElement == null) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 3, 1000);
                // 每隔500毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                linkElement = wait.until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver driver) {
                        try {
                            String curRegNum = driver.findElement(By.cssSelector("input[name='request:sn']")).getAttribute("value");
                            // System.out.println("result page regNum: " + curRegNum);
                            if (regNum.equals(curRegNum)) {
                                return driver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]"));
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            return null;
                        }
                    }
                });
            } catch (TimeoutException e) {
                switchWindows(driver, searchWinTitle);
                // System.out.println("====> redo submit...");
                submitBtn.submit();
                switchWindows(driver, resultWinTitle);
            }
        }
        linkElement.click();
        switchWindows(driver, detailWinTitle);

        // 切换到详情页，获取流程列表
        WebElement regFlowsEle = null;
        while (regFlowsEle == null) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 3, 1000);
                regFlowsEle = wait.until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        try {
                            String curRegNum = driver.findElement(By.cssSelector("input[name='info:rn']")).getAttribute("value");
                            // System.out.println("detail page regNum: " + curRegNum);
                            if (curRegNum.equals(regNum)) {
                                return driver.findElement(By.cssSelector("body>div.xqboxx>div>ul"));
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            return null;
                        }
                    }
                });
            } catch (TimeoutException e) {
                switchWindows(driver, resultWinTitle);
                // System.out.println("====> redo resultWin click: " + linkElement.findElement(By.cssSelector("a")).getText());
                linkElement.click();
                // driver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]")).click();
                switchWindows(driver, detailWinTitle);
            }
        }
        // switchWindows(driver, detailWinTitle);
        List<WebElement> regFlows = regFlowsEle.findElements(By.cssSelector("body>div.xqboxx>div>ul>li"));
        WebElement rejectDate = null;
        for (WebElement flow : regFlows) {
            WebElement element = flow.findElement(By.cssSelector("table>tbody>tr>td:nth-child(3)>span"));
            if (rejectionMark.equals(element.getText())) {
                rejectDate = flow.findElement(By.cssSelector("table>tbody>tr>td:nth-child(5)"));
            }
        }
        return rejectDate;
    }

    // 等待方法
    private static WebElement waitAndGet(FirefoxDriver driver, String xpath) {
        WebElement element = null;
        while (element == null) {

        }
        return null;
    }

    private static WebElement isAppear(WebDriver driver, String xPath, String conString) {
        return new WebDriverWait(driver, 3, 200).until(new ExpectedCondition<WebElement>() {
            @NullableDecl
            @Override
            public WebElement apply(@NullableDecl WebDriver webDriver) {
                if (webDriver.findElement(By.xpath(xPath)).getText().contains(conString)) {
                    return webDriver.findElement(By.xpath(xPath));
                } else {
                    return null;
                }
            }
        });
    }

    // 切换到指定窗口
    private static boolean switchWindows(WebDriver driver, String windowsTitle) {
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

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
