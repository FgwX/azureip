package com.azureip.tmspider.service;

import com.azureip.tmspider.exception.InitStatusQueryPageException;
import com.azureip.tmspider.exception.RetriedTooManyTimesException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RegistrationService {
    private static final Logger LOG = LogManager.getLogger(RegistrationService.class);

    private static final String SEARCH_WIN = "商标状态检索";
    private static final String RESULT_WIN = "商标检索结果";
    private static final String DETAIL_WIN = "商标详细内容";
    private static final String REJECT_MARK = "驳回通知发文";
    private static final String CHROME_DRIVER_DIR = "D:/Project/IDEA/azureip/tmspider/src/main/resources/drivers/chromedriver.exe";
    // private static final String CHROME_BIN_DIR = "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe";
    // private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    private static final String USER_AGENT_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    // private static final String FF_BIN_DIR = "C:/Program Files (x86)/Mozilla Firefox/firefox.exe";
    private static final String FF_DRIVER_DIR = "D:/Project/IDEA/azureip/tmspider/src/main/resources/drivers/geckodriver.exe";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    public List<String> optRejections(File srcDir, File tarDir) throws IOException {
        LOG.info("开始查询驳回数据...");
        File[] pendingFiles = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();

        // 初始化Selenium功能参数
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_DIR);
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);
        // System.setProperty("webdriver.firefox.bin", FF_BIN_DIR);

        for (int i = 0; i < (pendingFiles == null ? 0 : pendingFiles.length); i++) {
            String fileName = pendingFiles[i].getName();
            FileInputStream in = new FileInputStream(pendingFiles[i]);
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();
            LOG.info("[" + (i + 1) + "" + pendingFiles.length + "]开始处理《" + pendingFiles[i].getName() + "》...");
            try {
                queryRejectionAndAddLink(fileName, workBook);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("处理《" + fileName + "》时发生异常：" + e.getMessage());
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workBook.write(out);
            out.close();
            fileNames.add(fileName);
            LOG.info("[" + (i + 1) + "" + pendingFiles.length + "]《" + fileName + "》处理完成");
        }
        LOG.info("驳回数据处理完成！");
        return fileNames;
    }

    // 操作查询表格注册数据，标记驳回并添加链接
    private void queryRejectionAndAddLink(String fileName, XSSFWorkbook workBook) {
        String prefix = "[" + fileName + "] - ";
        XSSFSheet sheet = workBook.getSheetAt(0);
        WebDriver driver = initQueryPage();
        try {
            // 循环处理行（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                String regNum = row.getCell(0).getStringCellValue();
                if (StringUtils.isEmpty(regNum)) {
                    LOG.error(prefix + "的第" + (i + 1) + "行注册号为空！");
                    continue;
                }
                boolean success = false;
                while (!success) {
                    try {
                        success = queryRejectionData(driver, row, workBook);
                    } catch (RetriedTooManyTimesException e) {
                        quitBrowser(driver);
                        driver = initQueryPage();
                        LOG.error(prefix + "[" + regNum + "]重试次数过多，重新初始化查询...");
                    } catch (NoSuchElementException e) {
                        quitBrowser(driver);
                        driver = initQueryPage();
                        LOG.error(prefix + "[" + regNum + "]页面切换出错，重新初始化查询...");
                    }
                }
                // 速度控制
                threadWait(500);
            }
        } catch (StaleElementReferenceException e) {
            e.printStackTrace();
            LOG.error("StaleElementReferenceException: " + e.getMessage());
        }
        quitBrowser(driver);
    }

    // 初始化查询页面
    private WebDriver initQueryPage() {
        FirefoxOptions options = new FirefoxOptions();
        // FirefoxProfile profile = new ProfilesIni().getProfile("default");
        // profile.setPreference("general.useragent.override", USER_AGENT_IE);
        // options.setProfile(profile);
        options.addArguments("-safe-mode");
        FirefoxDriver driver = new FirefoxDriver(options);
        // ChromeDriver driver = new ChromeDriver();

        int retryTimes = 0;
        // 打开检索系统主页
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            retryTimes++;
            driver.get("http://wsjs.saic.gov.cn");
            try {
                statusQueryEle = new WebDriverWait(driver, 5, 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        // 选择商标状态查询
                        return driver.findElement(By.xpath("//*[@id='txnS03']"));
                    }
                });
            } catch (TimeoutException e) {
                LOG.error("重新打开[http://wsjs.saic.gov.cn]...");
            }
            if (retryTimes >= 5) {
                LOG.error("打开检索系统主页超时！");
                quitBrowser(driver);
                throw new InitStatusQueryPageException();
            }
        }
        statusQueryEle.click();
        // 等待页面加载完成（通过查询按钮判断页面是否加载完成）
        try {
            new WebDriverWait(driver, 15, 500).until(new ExpectedCondition<WebElement>() {
                @NullableDecl
                @Override
                public WebElement apply(@NullableDecl WebDriver driver) {
                    return driver.findElement(By.id("_searchButton"));
                }
            });
        } catch (TimeoutException e) {
            LOG.error("跳转到“状态查询”超时！");
            quitBrowser(driver);
            throw new InitStatusQueryPageException();
        }
        return driver;
    }

    // 查询驳回信息并添加链接
    private boolean queryRejectionData(WebDriver driver, XSSFRow row, XSSFWorkbook workbook) {
        String regNum = row.getCell(0).getStringCellValue();
        String prefix = "[" + regNum + "] - ";
        // 切换到查询页，输入注册号，进行查询
        switchWindows(driver, SEARCH_WIN);
        WebElement inputBox = driver.findElement(By.xpath("//*[@id='submitForm']//input[@name='request:sn']"));
        inputBox.clear();
        inputBox.sendKeys(regNum);
        WebElement submitBtn = driver.findElement(By.id("_searchButton"));
        submitBtn.submit();

        // 切换到结果页，等待结果加载完成后，点击详情页链接
        switchWindows(driver, RESULT_WIN);
        int resultQueryTimes = 0;
        WebElement resultEle = null;
        while (resultEle == null) {
            resultQueryTimes++;
            try {
                // 每隔500毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                resultEle = new WebDriverWait(driver, (resultQueryTimes > 1 ? 4 : 6), 500)
                        .until(new ExpectedCondition<WebElement>() {
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
                                    LOG.error("获取查询结果异常: " + e.getMessage());
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
            // 重试10次后，重新打开浏览器
            if (resultQueryTimes >= 10) {
                throw new RetriedTooManyTimesException();
            }
        }
        resultEle.click();

        // 切换到详情页，获取流程列表
        switchWindows(driver, DETAIL_WIN);
        int detailQueryTimes = 0;
        WebElement regFlowsEle = null;
        while (regFlowsEle == null) {
            detailQueryTimes++;
            try {
                WebDriverWait wait = new WebDriverWait(driver, (detailQueryTimes > 1 ? 4 : 6), 500);
                regFlowsEle = wait.until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(@NullableDecl WebDriver driver) {
                        try {
                            WebElement curRegNumEle = driver.findElement(By.xpath("//div[@id='detailParameter']/input[@name='info:rn']"));
                            WebElement requestID = driver.findElement(By.xpath("//input[@id='request_tid']"));
                            if (regNum.equals(curRegNumEle.getAttribute("value"))) {
                                return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                            } else if (!StringUtils.isEmpty(requestID.getAttribute("value"))) {
                                // 页面加载完成且未查询到流程数据，返回隐藏参数request:tid
                                return requestID;
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            LOG.error("获取流程列表异常: " + e.getMessage());
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
            if (detailQueryTimes >= 10) {
                throw new RetriedTooManyTimesException();
            }
        }

        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        // 如果返回Input[id=request_tid]，则说明此商标正等待受理，暂无法查询详细信息
        if ("request_tid".equals(regFlowsEle.getAttribute("id"))) {
            // 设置公告状态为“未受理”
            XSSFCell annStatCell = row.getCell(7) != null ? row.getCell(7) : row.createCell(7);
            annStatCell.setCellValue("未受理");
            LOG.info(prefix + "商标未受理，暂无详细信息");
        } else {
            // 设置链接
            XSSFCell tmNmeCell = row.getCell(4) != null ? row.getCell(4) : row.createCell(4);
            XSSFCellStyle linkStyle = workbook.createCellStyle();
            XSSFFont linkFont = workbook.createFont();
            linkFont.setUnderline((byte) 1);
            linkFont.setColor(IndexedColors.BLUE.index);
            linkStyle.setFont(linkFont);
            XSSFHyperlink hyperLink = creationHelper.createHyperlink(HyperlinkType.URL);
            hyperLink.setAddress(driver.getCurrentUrl());
            tmNmeCell.setHyperlink(hyperLink);
            tmNmeCell.setCellStyle(linkStyle);
            // 判断并记录驳回日期
            List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
            boolean hasRejection = false;
            for (WebElement flow : regFlows) {
                WebElement flowText = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[3]/span"));
                if (REJECT_MARK.equals(flowText.getText())) {
                    WebElement rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[5]"));
                    XSSFCell rejDateCell = row.getCell(6) != null ? row.getCell(6) : row.createCell(6);
                    try {
                        XSSFCellStyle dateStyle = workbook.createCellStyle();
                        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                        rejDateCell.setCellValue(dateFormat.parse(rejectDate.getText()));
                        rejDateCell.setCellStyle(dateStyle);
                    } catch (ParseException e) {
                        rejDateCell.setCellValue("日期转换异常");
                    }
                    hasRejection = true;
                    LOG.info(prefix + "查询到驳回，日期为：" + rejectDate.getText());
                }
            }
            if (!hasRejection) {
                LOG.info(prefix + "未查询到驳回");
            }
        }
        return true;
    }

    // 通过窗口标题切换窗口
    private void switchWindows(WebDriver driver, String tarWinTitle) {
        LOG.warn("[切换窗口] ==> " + tarWinTitle);
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

    // 通过句柄切换窗口
    private void switchTo(WebDriver driver, String targetWin) {
        //获取所有的窗口句柄
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            //切换并检查其title是否和目标窗口的title是否相同，是则返回true，否则继续
            if (window.equals(targetWin)) {
                driver.switchTo().window(window);
                return;
            }
        }
    }

    // 关闭所有窗口
    private void quitBrowser(WebDriver driver) {
        if (driver == null) {
            return;
        }
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle).close();
        }
        driver.quit();
    }

    // 线程等待
    private void threadWait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}