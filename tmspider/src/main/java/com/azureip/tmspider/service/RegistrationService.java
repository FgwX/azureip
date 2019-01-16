package com.azureip.tmspider.service;

import com.azureip.tmspider.exception.RetriedTooManyTimesException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
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
import java.util.logging.Level;

@Service
public class RegistrationService {
    private static final Logger LOG = LogManager.getLogger(RegistrationService.class);

    private static final String SEARCH_WIN = "商标状态检索";
    private static final String RESULT_WIN = "商标检索结果";
    private static final String DETAIL_WIN = "商标详细内容";
    private static final String REJECT_MARK = "驳回通知发文";
    private static final String CHROME_DRIVER_DIR;
    private static final String FF_DRIVER_DIR;
    // private static final String CHROME_BIN_DIR = "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe";
    // private static final String FF_BIN_DIR = "C:/Program Files (x86)/Mozilla Firefox/firefox.exe";
    // private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    // private static final String USER_AGENT_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    private static final SimpleDateFormat dateFormat;

    static {
        String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    }

    /**
     * 开始操作
     */
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
        WebDriver driver = quitAndRenewBrowser(null);

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
                    success = queryRejectionData(driver, workBook, i);
                } catch (RetriedTooManyTimesException e) {
                    LOG.error(prefix + "[" + regNum + "]重试次数过多，重新初始化...");
                    driver = quitAndRenewBrowser(driver);
                } catch (NoSuchElementException e) {
                    LOG.error(prefix + "[" + regNum + "]页面切换出错，重新初始化...");
                    driver = quitAndRenewBrowser(driver);
                } catch (StaleElementReferenceException e) {
                    LOG.error(prefix + "[" + regNum + "]操作元素已过期，重新初始化...");
                    driver = quitAndRenewBrowser(driver);
                } catch (Exception e) {
                    LOG.error(prefix + "[" + regNum + "]未知异常: ");
                    e.printStackTrace();
                }
            }
            // 设置随机等待时间，控制速度
            // Random random = new Random();
            // threadWait((500 + random.nextInt(1500)));
            threadWait(500);
        }
        quitBrowser(driver);
    }

    // 查询驳回信息并添加链接
    private boolean queryRejectionData(WebDriver driver, XSSFWorkbook workbook, int rowIndex) {
        switchWindows(driver, SEARCH_WIN);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int totalRows = sheet.getLastRowNum();
        XSSFRow row = sheet.getRow(rowIndex);
        String regNum = row.getCell(0).getStringCellValue();
        String prefix = "[" + rowIndex + "/" + totalRows + "]-[" + regNum + "] - ";
        if (rowIndex > 1
                && regNum.equals(sheet.getRow(rowIndex - 1).getCell(0).getStringCellValue())) {
            LOG.warn(prefix + "此行注册号与上一行相同，不再查询。");
            return true;
        }
        // 切换到查询页，输入注册号，进行查询
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
                resultEle = new WebDriverWait(driver, (resultQueryTimes > 1 ? 4 : 5), 500).until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver driver) {
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
            if (resultQueryTimes >= 8) {
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
                final int returnFlag = detailQueryTimes;
                regFlowsEle = new WebDriverWait(driver, (detailQueryTimes > 1 ? 4 : 5), 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        try {
                            WebElement curRegNumEle = driver.findElement(By.xpath("//div[@id='detailParameter']/input[@name='info:rn']"));
                            WebElement requestID = driver.findElement(By.xpath("//input[@id='request_tid']"));
                            if (regNum.equals(curRegNumEle.getAttribute("value"))) {
                                return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                            } else if (returnFlag >= 5 && !StringUtils.isEmpty(requestID.getAttribute("value"))) {
                                return requestID;
                            }
                            return null;
                        } catch (StaleElementReferenceException e) {
                            LOG.error("获取流程列表异常: " + e.getMessage());
                            return null;
                        }
                    }
                });
                // printLog(driver, prefix);
            } catch (TimeoutException | ElementNotInteractableException e) {
                LOG.debug("[248] - TimeoutException | ElementNotInteractableException");
                switchWindows(driver, RESULT_WIN);
                resultEle.click();
            } finally {
                switchWindows(driver, DETAIL_WIN);
            }
            if (detailQueryTimes >= 8) {
                throw new RetriedTooManyTimesException();
            }
        }

        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        // 如果返回Input[id=request_tid]，则说明此商标正等待受理，暂无法查询详细信息
        List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
        if ("request_tid".equals(regFlowsEle.getAttribute("id"))) {
            // if (regFlows == null || regFlows.size() == 0) {
            LOG.debug("[264] - 未受理");
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
            // 设置公告状态为“未受理”
            XSSFCell annStatCell = row.getCell(7) != null ? row.getCell(7) : row.createCell(7);
            annStatCell.setCellValue("未受理");
            LOG.info(prefix + "商标未受理，暂无详细信息");
        } else {
            LOG.debug("[281] - 判断是否驳回");
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
            boolean hasRejection = false;
            for (int i = 0; i < regFlows.size(); i++) {
                WebElement flow = regFlows.get(i);
                WebElement flowText = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[3]/span"));
                if (REJECT_MARK.equals(flowText.getText())) {
                    WebElement rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[5]"));
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

    // 关闭所有窗口
    private void quitBrowser(WebDriver driver) {
        if (driver == null) {
            return;
        }
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle).close();
        }
        // driver.quit();
    }

    // 关闭所有窗口并重新创建
    private WebDriver quitAndRenewBrowser(WebDriver driver) {
        if (driver == null) {
            while (driver == null) {
                driver = initQueryPage();
            }
            return driver;
        } else {
            Set<String> handles = driver.getWindowHandles();
            for (String handle : handles) {
                driver.switchTo().window(handle).close();
            }
            driver = null;
            while (driver == null) {
                driver = initQueryPage();
            }
            return driver;
        }
        // driver.quit();
    }

    // 初始化查询页面
    private WebDriver initQueryPage() {
        LOG.warn("正在初始化浏览器...");
        boolean useChrome = false;
        WebDriver driver = null;
        if (useChrome) {
            ChromeOptions options = new ChromeOptions();
            // DesiredCapabilities cap = DesiredCapabilities.chrome();
            // cap.setCapability(ChromeOptions.CAPABILITY, options);
            // LoggingPreferences logPref = new LoggingPreferences();
            // logPref.enable(LogType.PERFORMANCE, Level.ALL);
            // cap.setCapability(CapabilityType.LOGGING_PREFS, logPref);
            // options.setCapability(ChromeOptions.CAPABILITY, cap);
            driver = new ChromeDriver(options);
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

        driver.manage().window().setPosition(new Point(0, 0));
        // for Chrome
        // driver.manage().window().setSize(new Dimension(1002,538));
        // for Firefox
        driver.manage().window().setSize(new Dimension(1014,619));

        int retryTimes = 0;
        // 打开检索系统主页
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            retryTimes++;
            driver.get("http://wsjs.saic.gov.cn");
            try {
                statusQueryEle = new WebDriverWait(driver, 6, 500).until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver driver) {
                        // 选择商标状态查询
                        return driver.findElement(By.xpath("//*[@id='txnS03']"));
                    }
                });
            } catch (TimeoutException e) {
                LOG.error("重新打开[http://wsjs.saic.gov.cn]...");
            }
            if (retryTimes >= 3) {
                LOG.error("打开检索系统主页超时！");
                quitBrowser(driver);
                // throw new InitStatusQueryPageException();
                return null;
            }
        }
        statusQueryEle.click();
        // 等待页面加载完成（通过查询按钮判断页面是否加载完成）
        try {
            new WebDriverWait(driver, 12, 500).until(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(WebDriver driver) {
                    return driver.findElement(By.id("_searchButton"));
                }
            });
        } catch (TimeoutException e) {
            LOG.error("跳转到“状态查询”超时！");
            quitBrowser(driver);
            // throw new InitStatusQueryPageException();
            return null;
        }
        return driver;
    }

    // 通过窗口标题切换窗口
    private void switchWindows(WebDriver driver, String tarWinTitle) {
        // LOG.warn("[切换窗口] ==> " + tarWinTitle);
        //获取所有的窗口句柄
        Set<String> handles = driver.getWindowHandles();
        //获取当前窗口的句柄
        String currentHandle = driver.getWindowHandle();
        //获取当前窗口的title
        String currentTitle = driver.getTitle();
        if (currentTitle.equals(tarWinTitle)) {
            return;
        }
        for (String handle : handles) {
            //略过当前窗口
            if (handle.equals(currentHandle)) {
                continue;
            }
            //切换并检查其Title是否和目标窗口的Title是否相同
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

    // 线程等待
    private void threadWait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // PerformanceLog打印
    private void printLog(WebDriver driver, String prefix) {
        LogEntries logEntries = driver.manage().logs().get(LogType.PERFORMANCE);
        for (LogEntry req : logEntries) {
            JSONObject message = new JSONObject(req.getMessage()).getJSONObject("message");
            String method = message.getString("method");
            if (!StringUtils.isEmpty(method) && "Network.responseReceived".equals(method)) {
                JSONObject params = message.getJSONObject("params");
                JSONObject resp = params.getJSONObject("response");
                LOG.warn(prefix + "Resp: " + resp);
            }
        }
    }
}
