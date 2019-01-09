package com.azureip.tmspider.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
    // private static final String USER_AGENT_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
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
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFCreationHelper creationHelper = workbook.getCreationHelper();
            XSSFSheet sheet = workbook.getSheetAt(0);
            in.close();
            LOG.info("[" + (i + 1) + "" + pendingFiles.length + "]开始处理《" + pendingFiles[i].getName() + "》，"
                    + "共计" + sheet.getLastRowNum() + "条数据");
            List<XSSFRow> rows = new ArrayList<>();
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                rows.add(sheet.getRow(j));
            }
            try {
                operation(fileName, rows, creationHelper);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("处理《" + fileName + "》时发生异常：" + e.getMessage());
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workbook.write(out);
            out.close();
            fileNames.add(fileName);
            LOG.info("[" + (i + 1) + "" + pendingFiles.length + "]《" + fileName + "》处理完成");
        }
        LOG.info("驳回数据处理完成！");
        return fileNames;
    }

    // 操作查询表格注册数据，标记驳回并添加链接
    private void operation(String fileName, List<XSSFRow> rows, XSSFCreationHelper creationHelper) {
        WebDriver driver = initQueryPage();

        // 循环处理行
        for (int i = 0; i < rows.size(); i++) {
            String prefix = "[" + fileName + "]";
            XSSFRow row = rows.get(i);
            if (StringUtils.isEmpty(row.getCell(0).getStringCellValue())) {
                LOG.warn(prefix + "的第" + (i + 1) + "行注册号为空！");
                continue;
            }
            WebElement rejectDateEle = null;
            rejectDateEle = queryRejectionData(driver, row, creationHelper);
            if (rejectDateEle != null) {
                LOG.info(prefix + "的第" + (i + 1) + "行查询到驳回，日期为：" + rejectDateEle.getText());
            } else {
                LOG.info(prefix + "的第" + (i + 1) + "行未查询到驳回");
            }
        }
    }

    // 初始化查询页面
    private WebDriver initQueryPage() {
        // Firefox用户文件夹：C:/Users/%USER%/AppData/Local/Mozilla/Firefox/Profiles
        FirefoxOptions options = new FirefoxOptions();
        // options.setProfile(new ProfilesIni().getProfile("default"));
        FirefoxDriver driver = new FirefoxDriver(options);

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
                // driver.get("http://wsjs.saic.gov.cn");
            }
            if (retryTimes >= 5) {
                LOG.error("打开检索系统主页超时！");
                driver.quit();
                return null;
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
            driver.quit();
            return null;
        }
        return driver;
    }

    // 查询驳回信息并添加链接
    private WebElement queryRejectionData(WebDriver driver, XSSFRow row, XSSFCreationHelper creationHelper) {
        String regNum = row.getCell(0).getStringCellValue();
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
                resultEle = new WebDriverWait(driver, 3, 500).until(new ExpectedCondition<WebElement>() {
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
                            LOG.warn("Getting resultElement throws exception: \r\n" + e.getMessage());
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
                WebDriverWait wait = new WebDriverWait(driver, 3, 500);
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

        // 设置链接
        XSSFHyperlink hyperLink = creationHelper.createHyperlink(HyperlinkType.URL);
        hyperLink.setAddress(driver.getCurrentUrl());
        row.getCell(4).setHyperlink(hyperLink);
        // 判断并记录驳回日期
        List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
        WebElement rejectDate = null;
        for (WebElement flow : regFlows) {
            WebElement element = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[3]/span"));
            if (REJECT_MARK.equals(element.getText())) {
                // XSSFDataFormat dataFormat = creationHelper.createDataFormat();
                rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li/table/tbody/tr/td[5]"));
                try {
                    row.getCell(6).setCellValue(dateFormat.parse(rejectDate.getText()));
                } catch (ParseException e) {
                    LOG.error("日期转换异常！");
                    row.getCell(6).setCellValue("转换异常");
                }
            }
        }
        return rejectDate;
    }

    private void switchWindows(WebDriver driver, String tarWinTitle) {
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
    private WebElement waitAndGet(FirefoxDriver driver, String xpath) {
        WebElement element = null;
        while (element == null) {

        }
        return null;
    }

    // 线程等待
    public void threadWait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
