package com.azureip.tmspider.controller;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.StyledEditorKit;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("reg")
public class RegistrationController {

    @GetMapping("test")
    public void test() throws IOException {
        FileInputStream in = new FileInputStream("D:/test.xlsx");
        XSSFWorkbook excel = new XSSFWorkbook(in);
        in.close();
        XSSFSheet sheet = excel.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(3);
            System.out.println(cell.getStringCellValue());
            System.out.println(cell.getHyperlink().getAddress());
        }
    }

    private static final String SEARCH_WIN = "商标状态检索";
    private static final String RESULT_WIN = "商标检索结果";
    private static final String DETAIL_WIN = "商标详细内容";
    private static final String REJECT_MARK = "驳回通知发文";
    private static final String CHROME_DRIVER_DIR = "D:/Project/IDEA/azureip/tmspider/src/main/resources/drivers/chromedriver.exe";
    // private static final String CHROME_BIN_DIR = "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe";
    // private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    // private static final String USER_AGENT_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    // private static final String FF_BIN_DIR = "C:/Program Files (x86)/Mozilla Firefox/firefox.exe";
    private static final String FF_DRIVER_DIR = "D:/Project/IDEA/azureip/tmspider/src/main/resources/drivers/geckodriver.exe";

    /**
     * 商标网驳回信息查询
     */
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_DIR);
        // System.setProperty("webdriver.firefox.bin", FF_BIN_DIR);
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);

        List<String> regNums = new ArrayList<>();
        regNums.add("32613230");
        regNums.add("32613231");
        regNums.add("32613232");
        regNums.add("32613233");
        regNums.add("32613234");
        regNums.add("32613235");
        regNums.add("32613236");
        regNums.add("32613945");
        regNums.add("32614778");
        regNums.add("32614825");
        regNums.add("32615059");
        regNums.add("32615291");
        regNums.add("32615339");
        regNums.add("32616403");
        regNums.add("32616561");
        regNums.add("32617124");
        regNums.add("32617144");
        regNums.add("32617166");
        regNums.add("32617575");
        regNums.add("32617623");
        regNums.add("32617658");
        regNums.add("32617663");
        regNums.add("32617671");
        regNums.add("32618001");
        regNums.add("32618454");

        // ChromeOptions options = new ChromeOptions();
        // options.addArguments("user-data-dir=C:/Users/lewiszhang/AppData/Local/Google/Chrome/User Data");
        // ChromeDriver driver = new ChromeDriver();

        FirefoxOptions options = new FirefoxOptions();
        // C:/Users/lewiszhang/AppData/Local/Mozilla/Firefox/Profiles
        // options.setProfile(new ProfilesIni().getProfile("default"));
        FirefoxDriver driver = new FirefoxDriver(options);

        // driver.get("https://www.baidu.com");

        opteration(driver, regNums);
    }

    private static void opteration(WebDriver driver, List<String> regNums) {
        int retryTimes = 0;
        // 打开检索系统主页
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            retryTimes++;
            driver.get("http://wsjs.saic.gov.cn");
            try {
                statusQueryEle = new WebDriverWait(driver, 3, 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        // 选择商标状态查询
                        return driver.findElement(By.xpath("//*[@id='txnS03']"));
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
        statusQueryEle.click();
        // 等待页面加载完成
        new WebDriverWait(driver, 10, 1000).until(new ExpectedCondition<WebElement>() {
            @NullableDecl
            @Override
            public WebElement apply(@NullableDecl WebDriver driver) {
                return driver.findElement(By.xpath("//*[@id='submitForm']//input[@name='request:sn']"));
            }
        });

        for (int i = 0; i < regNums.size(); i++) {
            WebElement rejectDateEle = queryRejectDate(driver, regNums.get(i));
            String prefix = "[" + (i + 1) + "]" + regNums.get(i) + ": ";
            if (rejectDateEle != null) {
                System.out.println(prefix + "驳回通知发文日期为" + rejectDateEle.getText());
            } else {
                System.out.println(prefix + "未查询到驳回通知");
            }
        }
    }

    // 使用FirefoxDriver通过注册号查询驳回信息
    private static WebElement queryRejectDate(WebDriver driver, String regNum) {
        // 切换到查询页，输入注册号，进行查询
        switchWindows(driver, SEARCH_WIN);
        WebElement inputBox = driver.findElement(By.xpath("//*[@id='submitForm']//input[@name='request:sn']"));
        inputBox.clear();
        inputBox.sendKeys(regNum);
        WebElement submitBtn = driver.findElement(By.id("_searchButton"));
        submitBtn.submit();

        // 切换到结果页，等待结果加载完成后，点击详情页链接
        switchWindows(driver, RESULT_WIN);
        WebElement resultEle = null;
        while (resultEle == null) {
            try {
                // 每隔500毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                resultEle = new WebDriverWait(driver, 3, 1000).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        try {
                            if (regNum.equals(driver.findElement(By.xpath("//*[@id='request_sn']")).getAttribute("value"))) {
                                return driver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]/a"));
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            System.err.println("Getting resultElement throws exception: \r\n" + e.getMessage());
                            return null;
                        }
                    }
                });
            } catch (TimeoutException e) {
                switchWindows(driver, SEARCH_WIN);
                submitBtn.submit();
            } finally {
                switchWindows(driver, RESULT_WIN);
            }
        }
        resultEle.click();

        // 切换到详情页，获取流程列表
        switchWindows(driver, DETAIL_WIN);
        WebElement regFlowsEle = null;
        while (regFlowsEle == null) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 3, 1000);
                regFlowsEle = wait.until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        try {
                            WebElement curRegNumEle = driver.findElement(By.xpath("//*[@id='detailParameter']/input[@name='info:rn']"));
                            if (regNum.equals(curRegNumEle.getAttribute("value"))) {
                                return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            System.err.println("Getting regFlows throws exception: \r\n" + e.getMessage());
                            return null;
                        }
                    }
                });
            } catch (TimeoutException e) {
                switchWindows(driver, RESULT_WIN);
                resultEle.click();
            } finally {
                switchWindows(driver, DETAIL_WIN);
            }
        }

        List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
        System.out.println("regFlows's size: " + regFlows.size());
        WebElement rejectDate = null;
        for (WebElement flow : regFlows) {
            WebElement element = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[3]/span"));
            if (REJECT_MARK.equals(element.getText())) {
                rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[5]"));
            }
        }
        return rejectDate;
    }

    // 切换到指定窗口
    private static void switchWindows(WebDriver driver, String tarWinTitle) {
        //获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        //获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        //获取当前窗口的title
        String currentTitle = driver.getTitle();
        //要切换窗口为当前窗口则直接返回true
        if (currentTitle.equals(tarWinTitle)) {
            return;
        }
        //处理要切换到的窗口非当前窗口的情况
        for (String handle : handles) {
            //略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            //切换并检查其title是否和目标窗口的title是否相同，是则返回true，否则继续
            if ((driver.switchTo().window(handle).getTitle()).equals(tarWinTitle)) {
                return;
            }
        }
    }

    // 等待方法
    private static WebElement waitAndGet(FirefoxDriver driver, String xpath) {
        WebElement element = null;
        while (element == null) {

        }
        return null;
    }

    // 线程等待
    public static void threadWait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
