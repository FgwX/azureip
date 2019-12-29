package com.azureip.tmspider.service;

import com.azureip.common.constant.Constant;
import com.azureip.common.exception.ProxyIPBlockedException;
import com.azureip.common.exception.RetriedTooManyTimesException;
import com.azureip.common.service.SeleniumService;
import com.azureip.common.util.ExcelUtils;
import com.azureip.common.util.SeleniumUtils;
import com.azureip.ipspider.service.ProxyIPProvider;
import com.azureip.tmspider.constant.TMSConstant;
import com.azureip.tmspider.mapper.RejectionDataMapper;
import com.azureip.tmspider.model.RejectionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RejectionService {
    static {
        // String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        // CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        // FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    }

    private static final String WEB_DRIVER_TYPE = Constant.WEB_DRIVER_FIREFOX;
    private static final Logger LOG = LogManager.getLogger(RejectionService.class);
    private static final String SEARCH_WIN = "商标状态检索";
    private static final String RESULT_WIN = "商标检索结果";
    private static final String DETAIL_WIN = "商标详细内容";
    private static final String REJECT_MARK = "驳回通知发文";
    private static final String FIRST_TRIAL_MARK = "初审公告";
    private static final String UNTREATED_MARK = "未受理";
    // private static final String CHROME_DRIVER_DIR;
    // private static final String FF_DRIVER_DIR;
    private static final SimpleDateFormat dateFormat;
    @Autowired
    private ProxyIPProvider proxyIPProvider;
    @Autowired
    private SeleniumService seleniumService;
    @Autowired(required = false)
    private RejectionDataMapper rejectionDataMapper;

    /**
     * 处理表格（查询驳回）
     */
    public List<String> queryRejections(File srcDir, File tarDir) throws IOException {
        File[] pendingFiles = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();

        // 初始化Selenium功能参数
        // System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_DIR);
        // System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);
        // System.setProperty("webdriver.firefox.bin", FF_BIN_DIR);

        for (int i = 0; i < (pendingFiles == null ? 0 : pendingFiles.length); i++) {
            String fileName = pendingFiles[i].getName();
            FileInputStream in = new FileInputStream(pendingFiles[i]);
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();
            LOG.info("[" + (i + 1) + "/" + pendingFiles.length + "]开始处理《" + pendingFiles[i].getName() + "》...");
            try {
                queryRejections(fileName, workBook);
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
        return fileNames;
    }

    private void queryRejections(String fileName, XSSFWorkbook workBook) {
        XSSFSheet sheet = workBook.getSheetAt(0);
        WebDriver driver = SeleniumUtils.initBrowser(WEB_DRIVER_TYPE, null, 6000);

        // 循环处理行（跳过标题行）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            String regNum = row.getCell(0).getStringCellValue();
            String prefix = "[" + fileName + "]第" + (i + 1) + "行[" + regNum + "] - ";
            if (!rowIsValid(row, prefix)) {
                continue;
            }

            long start = System.currentTimeMillis();
            int retryTimes = 0;
            XSSFHyperlink hyperlink = row.getCell(4).getHyperlink();
            WebElement regFlowsEle = null;
            while (regFlowsEle == null) {
                try {
                    driver.get(hyperlink.getAddress());
                    regFlowsEle = new WebDriverWait(driver, 4, 500).until(new ExpectedCondition<WebElement>() {
                        @Override
                        public WebElement apply(WebDriver driver) {
                            try {
                                WebElement curRegNumEle = driver.findElement(By.xpath("//div[@id='detailParameter']/input[@name='info:rn']"));
                                if (regNum.equals(curRegNumEle.getAttribute("value"))) {
                                    return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                                }
                                return null;
                            } catch (StaleElementReferenceException e) {
                                LOG.error("获取流程列表异常: " + e.getMessage());
                                return null;
                            }
                        }
                    });
                } catch (TimeoutException | ElementNotInteractableException e) {
                    LOG.debug(prefix + "TimeoutException | ElementNotInteractableException: " + e.getMessage());
                }
                // 每行最多重试5次，或20秒
                if (++retryTimes >= 5 || (System.currentTimeMillis() - start) > 20000) {
                    break;
                }
            }

            // 流程判断，标记驳回
            XSSFCell rejDateCell = row.getCell(6) != null ? row.getCell(6) : row.createCell(6);
            if (regFlowsEle == null) {
                // 标记查询超时
                ExcelUtils.setText(workBook, rejDateCell, "查询超时");
                LOG.info(prefix + "查询超时");
            } else {
                XSSFCreationHelper creationHelper = workBook.getCreationHelper();
                List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));

                // 判断并记录驳回日期
                boolean hasRejection = false;
                for (int j = 0; j < regFlows.size(); j++) {
                    WebElement flow = regFlows.get(j);
                    WebElement flowText = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (j + 1) + "]/table/tbody/tr/td[3]/span"));
                    if (REJECT_MARK.equals(flowText.getText())) {
                        WebElement rejectDate = flow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (j + 1) + "]/table/tbody/tr/td[5]"));
                        // 设置驳回日期
                        ExcelUtils.setDate(workBook, rejDateCell, rejectDate.getText());
                        hasRejection = true;
                        LOG.info(prefix + "驳回日期为：" + rejectDate.getText());
                    }
                }
                if (!hasRejection) {
                    // 无驳回时置空
                    rejDateCell.setCellValue("");
                    LOG.info(prefix + "无驳回");
                }
            }

            // 设置随机等待时间，控制访问速度
            threadWait((100 + new Random().nextInt(1000)));
        }
        SeleniumUtils.quitBrowser(driver);
    }

    private boolean rowIsValid(XSSFRow row, String prefix) {
        if (StringUtils.isEmpty(row.getCell(0).getStringCellValue().trim())) {
            LOG.warn(prefix + "注册号为空");
            return false;
        }
        XSSFCell tmNameCell = row.getCell(4);
        if (tmNameCell == null) {
            LOG.warn(prefix + "商标名称为空");
            return false;
        } else if (tmNameCell.getHyperlink() == null || StringUtils.isEmpty(tmNameCell.getHyperlink().getAddress().trim())) {
            LOG.warn(prefix + "未添加链接");
            return false;
        }
        XSSFCell rejDateCell = row.getCell(6);
        if (rejDateCell != null && rejDateCell.getCellTypeEnum() != null) {
            if ((CellType.STRING.equals(rejDateCell.getCellTypeEnum()) && !StringUtils.isEmpty(rejDateCell.getStringCellValue().trim()))
                    || (CellType.NUMERIC.equals(rejDateCell.getCellTypeEnum()) && rejDateCell.getNumericCellValue() > 0)) {
                LOG.warn(prefix + "已驳回");
                return false;
            }
        }
        XSSFCell statusCell = row.getCell(7);
        if (statusCell != null && statusCell.getCellTypeEnum() != null && CellType.STRING.equals(statusCell.getCellTypeEnum())
                && (FIRST_TRIAL_MARK.equals(statusCell.getStringCellValue()) || UNTREATED_MARK.equals(statusCell.getStringCellValue()))) {
            LOG.warn(prefix + "状态为" + statusCell.getStringCellValue() + "");
            return false;
        }
        return true;
    }


    public void handleRejectionData() {
        LOG.info("==> 开始处理注册数据，查询驳回...");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -12);
        // 获取待查询的数据
        List<RejectionData> dataList = rejectionDataMapper.getPendingRejectionDatas(calendar.getTime(), 10000);

        // 查询驳回状态
        int size = dataList.size();
        WebDriver driver = seleniumService.initStatusQueryPage(null, WEB_DRIVER_TYPE, null, null);
        for (int i = 0; i < size; i++) {
            RejectionData data = dataList.get(i);
            String prefix = "[(" + (i + 1) + "/" + size + ")" + data.getRegNum() + ":" + data.getType() + "]";
            boolean success = false;
            while (!success) {
                try {
                    success = queryRejectionData(driver, data, prefix);
                } catch (RetriedTooManyTimesException e) {
                    LOG.error(prefix + "重试次数过多，重新初始化...");
                    driver = seleniumService.initStatusQueryPage(driver, WEB_DRIVER_TYPE, null, null);
                } catch (NoSuchElementException e) {
                    LOG.error(prefix + "页面切换出错，重新初始化...");
                    driver = seleniumService.initStatusQueryPage(driver, WEB_DRIVER_TYPE, null, null);
                } catch (StaleElementReferenceException e) {
                    LOG.error(prefix + "操作元素过期，重新初始化...");
                    driver = seleniumService.initStatusQueryPage(driver, WEB_DRIVER_TYPE, null, null);
                } catch (ProxyIPBlockedException e) {
                    LOG.error(prefix + "IP已被拦截，更换IP...");
                    driver = seleniumService.initStatusQueryPage(driver, WEB_DRIVER_TYPE, proxyIPProvider.nextProxy(), null);
                } catch (NoSuchSessionException e) {
                    LOG.error(prefix + "页面已被关闭！结束查询...");
                    SeleniumUtils.quitBrowser(driver);
                    return;
                } catch (Exception e) {
                    LOG.error(prefix + "未知异常（" + e.getClass() + "）: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            data.setCheckTime(new Date());
            if (!(rejectionDataMapper.updateByPrimaryKey(data) > 0)) {
                LOG.error(prefix + "数据库更新失败");
            }
            // 设置等待时间，控制速度
            threadWait(new Random().nextInt(1000));
        }
        SeleniumUtils.quitBrowser(driver);
    }

    private boolean queryRejectionData(WebDriver driver, RejectionData data, String prefix) {
        // 切换到查询页，输入注册号，进行查询
        SeleniumUtils.switchByTitle(driver, SEARCH_WIN);
        WebElement inputBox = driver.findElement(By.xpath("//*[@id='submitForm']//input[@name='request:sn']"));
        inputBox.clear();
        inputBox.sendKeys(data.getRegNum());
        WebElement submitBtn = driver.findElement(By.id("_searchButton"));
        submitBtn.submit();

        // 切换到结果页，等待结果加载完成后，点击详情页链接
        SeleniumUtils.switchByTitle(driver, RESULT_WIN);
        int resultRetryTimes = 0;
        WebElement resultEle = null;
        while (resultEle == null) {
            try {
                // 每隔500毫秒去调用一下until中的函数，默认是0.5秒，如果等待3秒还没有找到元素，则抛出异常。
                resultEle = new WebDriverWait(driver, (resultRetryTimes++ > 1 ? 3 : 5), 500).until(new ExpectedCondition<WebElement>() {
                    @NullableDecl
                    @Override
                    public WebElement apply(WebDriver driver) {
                        try {
                            if (data.getRegNum().equals(driver.findElement(By.xpath("//*[@id='request_sn']")).getAttribute("value"))) {
                                return driver.findElement(By.xpath("//*[@id='list_box']/table/tbody/tr[2]/td[2]/a"));
                            } else if (SeleniumUtils.blockedByHost(driver.getPageSource())) {
                                throw new ProxyIPBlockedException();
                            } else {
                                return null;
                            }
                        } catch (StaleElementReferenceException e) {
                            LOG.error(prefix + "结果页元素已过期: " + e.getMessage());
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
            if (resultRetryTimes >= 5) {
                LOG.info(prefix + "查询超时");
                data.setTimeout(true);
                data.setTreated(true);
                return true;
            }
        }
        // threadWait(200);
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
                            if (data.getRegNum().equals(curRegNumEle.getAttribute("value"))) {
                                return driver.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul"));
                            } else if (SeleniumUtils.blockedByHost(driver.getPageSource())) {
                                throw new ProxyIPBlockedException();
                            } else if (retryTimes >= 8 && !StringUtils.isEmpty(requestID.getAttribute("value"))) {
                                return requestID;
                            }
                            return null;
                        } catch (StaleElementReferenceException e) {
                            LOG.error(prefix + "获取流程列表异常: " + e.getMessage());
                            return null;
                        }
                    }
                });
            } catch (TimeoutException | ElementNotInteractableException e) {
                LOG.debug(prefix + " - TimeoutException | ElementNotInteractableException");
                SeleniumUtils.switchByTitle(driver, RESULT_WIN);
                resultEle.click();
            } finally {
                SeleniumUtils.switchByTitle(driver, DETAIL_WIN);
            }
            /*if (detailRetryTimes >= 5) {
                break;
            }*/
        }

        if ("request_tid".equals(regFlowsEle.getAttribute("id"))) {
            // 未受理（返回Input[id=request_tid]，则说明此商标正等待受理，暂无法查询详细信息）
            LOG.error(prefix + "商标未受理");
            data.setTimeout(false);
            data.setTreated(false);
        } else {
            // 判断是否驳回
            List<WebElement> regFlows = regFlowsEle.findElements(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li"));
            boolean rejected = false, reviewed = false, reviewExpired = false;
            Date rejDate = null;
            for (int i = 0; i < regFlows.size(); i++) {
                WebElement regFlow = regFlows.get(i);
                WebElement business = regFlow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[2]/span"));
                WebElement flow = regFlow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[3]/span"));
                if ("驳回复审".equals(business.getText()) && "申请收文".equals(flow.getText())) {
                    reviewed = true;
                }
                if ("商标注册申请".equals(business.getText())) {
                    if ("等待驳回复审".equals(flow.getText())) {
                        reviewExpired = true;
                    } else if ("驳回通知发文".equals(flow.getText())) {
                        rejected = true;
                        WebElement rejectDate = regFlow.findElement(By.xpath("/html/body/div[@class='xqboxx']/div/ul/li[" + (i + 1) + "]/table/tbody/tr/td[5]"));
                        try {
                            rejDate = TMSConstant.REJ_DATE_FMT.parse(rejectDate.getText());
                        } catch (ParseException e) {
                            LOG.error(prefix + "解析驳回日期异常[ParseException]：" + e.getMessage() + "，设置默认日期[1970-01-01 08:00:00]");
                            rejDate = TMSConstant.DEF_DATE;
                        }
                    }
                }
            }
            if (rejected) {
                LOG.warn(prefix + "已驳回，驳回日期为：" + TMSConstant.REJ_DATE_FMT.format(rejDate));
                data.setRejected(true);
                data.setRejectDate(rejDate);
                if (reviewed || reviewExpired) {
                    LOG.warn(prefix + "已复审或已过期");
                    data.setReviewed(true);
                }
            } else {
                LOG.info(prefix + "无驳回");
            }
            data.setTimeout(false);
            data.setTreated(true);
        }
        return true;
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
