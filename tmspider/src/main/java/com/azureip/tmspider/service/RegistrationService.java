package com.azureip.tmspider.service;

import com.azureip.common.constant.Constant;
import com.azureip.common.exception.RetriedTooManyTimesException;
import com.azureip.common.util.ExcelUtils;
import com.azureip.common.util.SeleniumUtils;
import com.azureip.tmspider.constant.TMSConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RegistrationService {
    static {
        String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
        // 请求中断操作标志
        interruptRequired = false;
        // 操作停止标志
        stopped = true;
    }

    private static final String WEB_DRIVER_TYPE = Constant.WEB_DRIVER_HTML;
    private static boolean interruptRequired;
    private static boolean stopped;
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

    /**
     * 处理表格（查询驳回，添加链接）
     */
    public List<String> optRejections(File srcDir, File tarDir) throws IOException {
        LOG.info("开始处理表格（查询驳回，添加链接）...");
        stopped = false;
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
            LOG.info("[" + (i + 1) + "/" + pendingFiles.length + "]开始处理《" + pendingFiles[i].getName() + "》...");
            try {
                queryRejectionAndAddLink(fileName, workBook);
            } catch (Exception e) {
                LOG.error("处理《" + fileName + "》时发生异常（" + e.getClass() + "）：" + e.getMessage());
                e.printStackTrace();
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workBook.write(out);
            out.close();
            fileNames.add(fileName);
            LOG.info("[" + (i + 1) + "" + pendingFiles.length + "]《" + fileName + "》处理完成");
        }
        LOG.info("表格处理（查询驳回，添加链接）完成！");
        return fileNames;
    }

    /**
     * 中断操作，保存文件
     */
    public boolean interruptOpt() {
        interruptRequired = true;
        long start = System.currentTimeMillis();
        while (true) {
            threadWait(500);
            if (System.currentTimeMillis() - start > 300000) {
                interruptRequired = false;
                return false;
            } else if (stopped) {
                interruptRequired = false;
                return true;
            }
        }
    }

    // 操作查询表格注册数据，标记驳回并添加链接
    private void queryRejectionAndAddLink(String fileName, XSSFWorkbook workBook) {
        XSSFSheet sheet = workBook.getSheetAt(0);
        WebDriver driver = reInitBrowser(null);

        // 循环处理行（跳过标题行）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            String regNum = row.getCell(0).getStringCellValue();
            if (StringUtils.isEmpty(regNum)) {
                LOG.error("[" + fileName + "]第" + (i + 1) + "行注册号为空！");
                continue;
            }
            String prefix = "[" + fileName + "]第" + (i + 1) + "行[" + regNum + "] - ";
            if (existsLinkOrUntreated(row, prefix)) {
                continue;
            }
            boolean success = false;
            while (!success) {
                try {
                    success = queryRejectionData(driver, workBook, i);
                } catch (RetriedTooManyTimesException e) {
                    LOG.error(prefix + "[" + regNum + "]重试次数过多，重新初始化...");
                    driver = reInitBrowser(driver);
                } catch (NoSuchElementException e) {
                    // LOG.error(prefix + "[" + regNum + "]页面切换出错，结束查询...");
                    LOG.error(prefix + "[" + regNum + "]页面切换出错，重新初始化...");
                    driver = reInitBrowser(driver);
                } catch (StaleElementReferenceException e) {
                    LOG.error(prefix + "[" + regNum + "]操作元素过期，重新初始化...");
                    driver = reInitBrowser(driver);
                } catch (NoSuchSessionException e) {
                    LOG.error(prefix + "[" + regNum + "]页面已被关闭！结束查询，保存表格...");
                    SeleniumUtils.quitBrowser(driver);
                    return;
                } catch (Exception e) {
                    LOG.error(prefix + "[" + regNum + "]未知异常（" + e.getClass() + "）: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // 设置等待时间，控制速度
            threadWait(new Random().nextInt(2000));
        }
        SeleniumUtils.quitBrowser(driver);
    }

    private boolean existsLinkOrUntreated(XSSFRow row, String prefix) {
        // 判断是否已添加链接
        /*XSSFCell tmNameCell = row.getCell(4);
        if (tmNameCell != null && tmNameCell.getHyperlink() != null && !StringUtils.isEmpty(tmNameCell.getHyperlink().getAddress().trim())) {
            LOG.warn(prefix + "已添加链接！");
            return true;
        }*/
        // 判断是否为未受理
        XSSFCell statusCell = row.getCell(7);
        if (statusCell != null && statusCell.getCellTypeEnum() != null && CellType.STRING.equals(statusCell.getCellTypeEnum())
                && ("未受理".equals(statusCell.getStringCellValue()))) {
            LOG.warn(prefix + "状态为" + statusCell.getStringCellValue() + "！");
            return true;
        }
        // 判断是否已有驳回和驳回日期
        XSSFCell rejDateCell = row.getCell(6);
        if (rejDateCell != null && rejDateCell.getCellTypeEnum() != null && CellType.NUMERIC.equals(rejDateCell.getCellTypeEnum()) && rejDateCell.getDateCellValue() != null) {
            LOG.warn(prefix + "已有驳回，日期为：" + rejDateCell.getDateCellValue());
            return true;
        }
        return false;
    }

    // 查询驳回信息并添加链接
    private boolean queryRejectionData(WebDriver driver, XSSFWorkbook workbook, int rowIndex) {
        // LOG.debug("当前窗口标题为：" + driver.getTitle());
        SeleniumUtils.switchByTitle(driver, SEARCH_WIN);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int totalRows = sheet.getLastRowNum();
        XSSFRow row = sheet.getRow(rowIndex);
        String regNum = row.getCell(0).getStringCellValue();
        String prefix = "[" + rowIndex + "/" + totalRows + "]-[" + regNum + "] - ";
        /*if (rowIndex > 1 && regNum.equals(sheet.getRow(rowIndex - 1).getCell(0).getStringCellValue())) {
            LOG.warn(prefix + "此行注册号与上一行相同，不再查询。");
            return true;
        }*/
        // 切换到查询页，输入注册号，进行查询
        WebElement inputBox = driver.findElement(By.xpath("//*[@id='submitForm']//input[@name='request:sn']"));
        inputBox.clear();
        inputBox.sendKeys(regNum);
        WebElement submitBtn = driver.findElement(By.id("_searchButton"));
        submitBtn.submit();

        // 切换到结果页，等待结果加载完成后，点击详情页链接
        SeleniumUtils.switchByTitle(driver, RESULT_WIN);
        int resultRetryTimes = 0;
        XSSFCell tmNameCell = row.getCell(4) != null ? row.getCell(4) : row.createCell(4);
        XSSFCell rejDateCell = row.getCell(6) != null ? row.getCell(6) : row.createCell(6);
        XSSFCell annStatCell = row.getCell(7) != null ? row.getCell(7) : row.createCell(7);
        WebElement resultEle = null;
        while (resultEle == null) {
            try {
                // 每隔500毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                resultEle = new WebDriverWait(driver, (resultRetryTimes++ > 1 ? 3 : 5), 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        try {
                            if (regNum.equals(driver.findElement(By.xpath("//*[@id='request_sn']")).getAttribute("value"))) {
                                return driver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]/a"));
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            LOG.error("结果页元素已过期: " + e.getMessage());
                            return null;
                        }
                    }
                });
            } catch (TimeoutException e) {
                SeleniumUtils.switchByTitle(driver, SEARCH_WIN);
                submitBtn.submit();
            } finally {
                SeleniumUtils.switchByTitle(driver, RESULT_WIN);
            }
            // 重试5次后，重新打开浏览器
            if (resultRetryTimes >= 8) {
                // throw new RetriedTooManyTimesException();
                LOG.info(prefix + "查询超时");
                ExcelUtils.setText(workbook, annStatCell, "查询超时");
                return true;
            }
        }
        threadWait(200);
        resultEle.click();

        // 切换到详情页，获取流程列表
        SeleniumUtils.switchByTitle(driver, DETAIL_WIN);
        int detailRetryTimes = 0;
        WebElement regFlowsEle = null;
        while (regFlowsEle == null) {
            final int retryTimes = ++detailRetryTimes;
            try {
                regFlowsEle = new WebDriverWait(driver, (detailRetryTimes > 1 ? 3 : 5), 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        try {
                            WebElement curRegNumEle = driver.findElement(By.xpath("//div[@id='detailParameter']/input[@name='info:rn']"));
                            WebElement requestID = driver.findElement(By.xpath("//input[@id='request_tid']"));
                            if (regNum.equals(curRegNumEle.getAttribute("value"))) {
                                return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                            } else if (retryTimes >= 8 && !StringUtils.isEmpty(requestID.getAttribute("value"))) {
                                return requestID;
                            }
                            return null;
                        } catch (StaleElementReferenceException e) {
                            LOG.error("获取流程列表异常: " + e.getMessage());
                            return null;
                        }
                    }
                });
            } catch (TimeoutException | ElementNotInteractableException e) {
                LOG.debug("[248] - TimeoutException | ElementNotInteractableException");
                SeleniumUtils.switchByTitle(driver, RESULT_WIN);
                resultEle.click();
            } finally {
                SeleniumUtils.switchByTitle(driver, DETAIL_WIN);
            }
            /*if (detailRetryTimes >= 5) {
                break;
            }*/
        }

        if (regFlowsEle == null) {
            // 查询超时
            ExcelUtils.setText(workbook, annStatCell, "查询超时");
            LOG.info(prefix + "查询超时");
        } else if ("request_tid".equals(regFlowsEle.getAttribute("id"))) {
            // 未受理（返回Input[id=request_tid]，则说明此商标正等待受理，暂无法查询详细信息）
            // ExcelUtils.setHyperLink(workbook, tmNmeCell, driver.getCurrentUrl());
            ExcelUtils.setText(workbook, annStatCell, "未受理");
            LOG.info(prefix + "商标未受理");
        } else {
            // 判断是否驳回
            // ExcelUtils.setHyperLink(workbook, tmNmeCell, driver.getCurrentUrl());
            List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
            boolean hasRejection = false;
            for (int i = 0; i < regFlows.size(); i++) {
                WebElement flow = regFlows.get(i);
                WebElement flowText = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[3]/span"));
                if (REJECT_MARK.equals(flowText.getText())) {
                    WebElement rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[5]"));
                    ExcelUtils.setDate(workbook, rejDateCell, rejectDate.getText().trim());
                    hasRejection = true;
                    LOG.info(prefix + "驳回日期为：" + rejectDate.getText());
                }
            }
            if (!hasRejection) {
                LOG.info(prefix + "无驳回");
            }
        }
        // 关闭无效页面
        // SeleniumUtils.closeAllButQueryPage(driver);
        return true;
    }

    // 关闭所有窗口并重新创建
    private WebDriver reInitBrowser(WebDriver driver) {
        if (driver != null) {
            SeleniumUtils.quitBrowser(driver);
        }
        driver = null;
        while (driver == null) {
            driver = initQueryPage();
        }
        return driver;
    }

    // 初始化查询页面
    private WebDriver initQueryPage() {
        LOG.warn("正在初始化浏览器...");
        WebDriver driver = SeleniumUtils.initBrowser(Constant.WEB_DRIVER_CHROME, null, null);
        int retryTimes = 0;
        // 打开检索系统主页
        WebElement statusQueryEle = null;
        while (statusQueryEle == null) {
            try {
                driver.get(TMSConstant.STATUS_DOMAIN);
                statusQueryEle = new WebDriverWait(driver, 10, 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        // 选择商标状态查询
                        return driver.findElement(By.xpath("//*[@id='txnS03']"));
                    }
                });
            } catch (TimeoutException e) {
                LOG.error("重新打开[http://wsjs.saic.gov.cn]...");
            }
            if (retryTimes++ >= 5) {
                LOG.error("打开检索系统主页超时！");
                SeleniumUtils.quitBrowser(driver);
                return null;
            }
        }
        statusQueryEle.click();
        // 等待页面加载完成（通过查询按钮判断页面是否加载完成）
        try {
            new WebDriverWait(driver, 12, 500).until(new ExpectedCondition<WebElement>() {
                @NullableDecl
                @Override
                public WebElement apply(WebDriver driver) {
                    return driver.findElement(By.id("_searchButton"));
                }
            });
        } catch (TimeoutException e) {
            LOG.error("跳转到“状态查询”超时！");
            SeleniumUtils.quitBrowser(driver);
            // throw new InitStatusQueryPageException();
            return null;
        }
        return driver;
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
