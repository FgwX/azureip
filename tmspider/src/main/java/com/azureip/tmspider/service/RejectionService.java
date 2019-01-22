package com.azureip.tmspider.service;

import com.azureip.tmspider.util.SeleniumUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.*;
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

@Service
public class RejectionService {
    static {
        String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    }

    private static final Logger LOG = LogManager.getLogger(RejectionService.class);
    private static final String REJECT_MARK = "驳回通知发文";
    private static final String FIRST_TRIAL_MARK = "初审公告";
    private static final String UNTREATED_MARK = "未受理";
    private static final String CHROME_DRIVER_DIR;
    private static final String FF_DRIVER_DIR;
    private static final SimpleDateFormat dateFormat;

    /**
     * 处理表格（查询驳回）
     */
    public List<String> queryRejections(File srcDir, File tarDir) throws IOException {
        LOG.info("开始处理表格（查询驳回）...");
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
                queryRejections(fileName, workBook);
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
        LOG.info("表格处理（查询驳回）完成！");
        return fileNames;
    }

    private void queryRejections(String fileName, XSSFWorkbook workBook) {
        XSSFSheet sheet = workBook.getSheetAt(0);
        WebDriver driver = SeleniumUtil.initDriver(false);

        // 循环处理行（跳过标题行）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            String prefix = "[" + fileName + "]第" + (i + 1) + "行 - ";
            XSSFRow row = sheet.getRow(i);
            if (!rowIsValid(row, prefix)) {
                continue;
            }

            int retryTimes = 0;
            String regNum = row.getCell(0).getStringCellValue();
            XSSFHyperlink hyperlink = row.getCell(4).getHyperlink();
            WebElement regFlowsEle = null;

            while (regFlowsEle == null) {
                /*if (retryTimes++ > 0 && regFlowsEle == null) {
                    LOG.info(prefix + "refreshing...");
                    driver.navigate().refresh();
                }*/
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
                // 每行最多重试5次
                if (retryTimes >= 5) {
                    break;
                }
            }

            // 流程判断，标记驳回
            XSSFCell rejDateCell = row.getCell(6) != null ? row.getCell(6) : row.createCell(6);
            if (regFlowsEle == null) {
                // 标记查询超时
                LOG.info(prefix + "查询超时");
                rejDateCell.setCellValue("查询超时");
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
                        try {
                            XSSFCellStyle dateStyle = workBook.createCellStyle();
                            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                            rejDateCell.setCellValue(dateFormat.parse(rejectDate.getText()));
                            rejDateCell.setCellStyle(dateStyle);
                        } catch (ParseException e) {
                            rejDateCell.setCellValue("转换异常");
                        }
                        hasRejection = true;
                        LOG.info(prefix + "查询到驳回，日期为：" + rejectDate.getText());
                    }
                }
                if (!hasRejection) {
                    LOG.info(prefix + "未查询到驳回");
                }
            }

            // 设置随机等待时间，控制速度
            // Random random = new Random();
            // threadWait((300 + random.nextInt(1200)));
            threadWait(300);
        }
        SeleniumUtil.quitBrowser(driver);
    }

    private boolean rowIsValid(XSSFRow row, String prefix) {
        String regNum = row.getCell(0).getStringCellValue();
        if (StringUtils.isEmpty(row.getCell(0).getStringCellValue().trim())) {
            LOG.warn(prefix + "注册号为空！");
            return false;
        }
        XSSFCell tmNameCell = row.getCell(4);
        if (tmNameCell != null && (tmNameCell.getHyperlink() == null
                || StringUtils.isEmpty(tmNameCell.getHyperlink().getAddress().trim()))) {
            LOG.warn(prefix + "未添加链接！");
            return false;
        }
        XSSFCell rejDateCell = row.getCell(6);
        if (rejDateCell != null && rejDateCell.getCellTypeEnum() != null) {
            if ((CellType.STRING.equals(rejDateCell.getCellTypeEnum()) && !StringUtils.isEmpty(rejDateCell.getStringCellValue().trim()))
                    || (CellType.NUMERIC.equals(rejDateCell.getCellTypeEnum()) && rejDateCell.getNumericCellValue() > 0)) {
                LOG.warn(prefix + "驳回日期不为空！");
                return false;
            }
        }
        XSSFCell statusCell = row.getCell(7);
        if (statusCell != null && statusCell.getCellTypeEnum() != null && CellType.STRING.equals(statusCell.getCellTypeEnum())
                && (FIRST_TRIAL_MARK.equals(statusCell.getStringCellValue()) || UNTREATED_MARK.equals(statusCell.getStringCellValue()))) {
            LOG.warn(prefix + "状态为“未受理”或“初审公告”！");
            return false;
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
