package com.azureip.tmspider.service;

import com.alibaba.druid.util.StringUtils;
import com.azureip.tmspider.mapper.AnnouncementMapper;
import com.azureip.tmspider.mapper.ExcelOptRecordMapper;
import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.model.ExcelOptRecord;
import com.azureip.tmspider.pojo.AnnListPojo;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Service
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final ExcelOptRecordMapper excelOptRecordMapper;
    //private ThreadPoolTaskExecutor executor;

    private static Gson gson = new Gson();
    // 请求头User-Agent
    private static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
    private static final String FIRST_TRIAL_ANN = "初审公告";

    @Autowired
    public AnnouncementService(AnnouncementMapper announcementMapper, ExcelOptRecordMapper excelOptRecordMapper) {
        this.announcementMapper = announcementMapper;
        this.excelOptRecordMapper = excelOptRecordMapper;
    }

    /**
     * 查询本地公告中最新一期的期号
     */
    public String queryLocalLatestAnnNum() {
        return announcementMapper.queryLocalLatestAnnNum();
    }

    /**
     * 多条件查询公告总数
     */
    public List<Integer> queryAnnCountTest(AnnQueryPojo pojo){
        List<Integer> resultList = new ArrayList<>();
        System.out.println(pojo.getAnnNum());
        int i = announcementMapper.queryAnnCountByAnnNum(pojo.getAnnNum());
        System.out.println(i);
        resultList.add(i);
        resultList.add(99999);
        return resultList;
    }

    /**
     * 多条件查询公告总数
     */
    public List<Integer> queryAnnCount(AnnQueryPojo pojo) throws IOException {
        List<Integer> resultList = new ArrayList<>();

        // 查询本地公告数量
        resultList.add(announcementMapper.queryAnnCountByAnnNum(pojo.getAnnNum()));

        // 查询商标网公告数量
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        StringBuilder countUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
        countUrl.append("?page=1&rows=1").append("&annNum=").append(pojo.getAnnNum()).append("&annType=").append(pojo.getAnnType()).append("&totalYOrN=true");
        if (!StringUtils.isEmpty(pojo.getAppDateBegin()) && !StringUtils.isEmpty(pojo.getAppDateEnd())) {
            countUrl.append("&appDateBegin=").append(pojo.getAppDateBegin()).append("&appDateEnd=").append(pojo.getAppDateEnd());
        }
        System.out.println("总量查询URL：" + countUrl.toString());
        HttpPost countPost = new HttpPost(countUrl.toString());
        countPost.setHeader("User-Agent", AGENT);
        countPost.setConfig(config);
        CloseableHttpResponse countResp = client.execute(countPost);
        AnnListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnListPojo.class);
        client.close();

        resultList.add(countPojo.getTotal());
        return resultList;
    }

    /**
     * 导入公告数据
     */
    @Transactional
    public int importAnns(AnnQueryPojo queryPojo) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        if (queryPojo.getTotal() < 1) {
            StringBuilder countUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            countUrl.append("?page=1&rows=1").append("&annNum=").append(queryPojo.getAnnNum()).append("&annType=").append(queryPojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(queryPojo.getAppDateBegin()) && !StringUtils.isEmpty(queryPojo.getAppDateEnd())) {
                countUrl.append("&appDateBegin=").append(queryPojo.getAppDateBegin()).append("&appDateEnd=").append(queryPojo.getAppDateEnd());
            }
            System.out.println("总量查询URL：" + countUrl.toString());
            HttpPost countPost = new HttpPost(countUrl.toString());
            countPost.setHeader("User-Agent", AGENT);
            countPost.setConfig(config);
            CloseableHttpResponse countResp = client.execute(countPost);
            AnnListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnListPojo.class);
            queryPojo.setTotal(countPojo.getTotal());
        }
        int times = (int) Math.ceil(queryPojo.getTotal() / (double) queryPojo.getRows());
        int successCount = 0;
        for (int i = 0; i < times; i++) {
            StringBuilder listUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            listUrl.append("?page=").append(i + 1).append("&rows=").append(queryPojo.getRows()).append("&annNum=").append(queryPojo.getAnnNum()).append("&annType=").append(queryPojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(queryPojo.getAppDateBegin()) && !StringUtils.isEmpty(queryPojo.getAppDateEnd())) {
                listUrl.append("&appDateBegin=").append(queryPojo.getAppDateBegin()).append("&appDateEnd=").append(queryPojo.getAppDateEnd());
            }
            String prefix = "[第" + (i + 1) + "次请求]";
            System.out.println(prefix + "URL: " + listUrl.toString());
            HttpPost post = new HttpPost(listUrl.toString());
            post.setHeader("User-Agent", AGENT);
            post.setHeader("Connection", "keep-alive");
            post.setConfig(config);
            long listQureyStart = System.currentTimeMillis();
            CloseableHttpResponse resp = client.execute(post);
            long listQueryEnd = System.currentTimeMillis();
            System.out.println(prefix + "请求响应耗时: " + (listQueryEnd - listQureyStart) + "毫秒");
            AnnListPojo annList = gson.fromJson(EntityUtils.toString(resp.getEntity()), AnnListPojo.class);
            long assembleEnd = System.currentTimeMillis();
            System.out.println(prefix + "拼装数据耗时: " + (assembleEnd - listQueryEnd) + "毫秒");
            int result;
            try {
                result = batchSave(annList.getRows());
            } catch (Exception e) {
                System.err.println("数据库异常: " + e.getMessage());
                return 0;
            }
            long insertEnd = System.currentTimeMillis();
            System.out.println(prefix + "插入数据耗时: " + (insertEnd - assembleEnd) + "毫秒");
            successCount += result;
        }
        client.close();
        System.out.println("插入成功！共计: " + successCount + "条公告。");
        return successCount;
    }

    /**
     * 处理EXCEL表格，标注已有初审公告的行
     */
    @Transactional
    public List<String> optExcel(File srcDir, File tarDir) throws IOException {
        File[] files = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        int totalExistCount = 0, totalProcCount = 0;
        for (int i = 0; i < (files != null ? files.length : 0); i++) {
            String fileName = files[i].getName();
            FileInputStream in = new FileInputStream(files[i]);
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            in.close();
            // 首行为标题行; 第B列为注册号，G列为公告状态
            XSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("【" + (i + 1) + "/" + files.length + "】开始处理《" + fileName + "》，共计" + sheet.getLastRowNum() + "条数据...");
            int existCount = 0, markCount = 0, dotCount = 0;
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                XSSFCell regNumCell = row.getCell(1);
                if (null != regNumCell && !StringUtils.isEmpty(regNumCell.getStringCellValue())) {
                    XSSFCell annStatCell = row.getCell(6);
                    if (null != annStatCell && FIRST_TRIAL_ANN.equals(annStatCell.getStringCellValue())) {
                        // 已标记过初审公告状态
                        System.out.print(":");
                        existCount++;
                    } else {
                        String regNum = regNumCell.getStringCellValue();
                        int annCount = getCountByRegNum(regNum);
                        if (annCount > 0) {
                            row.createCell(6).setCellValue(FIRST_TRIAL_ANN);
                            markCount++;
                            System.out.print("!");
                        } else {
                            System.out.print(".");
                        }
                    }
                } else {
                    System.err.println("正在处理第" + j + "行，此行注册号为空！");
                }
                // 换行处理
                dotCount++;
                if (dotCount >= 100) {
                    dotCount = 0;
                    System.out.println();
                }
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workbook.write(out);
            out.close();
            fileNames.add(fileName);
            totalExistCount += existCount;
            totalProcCount += markCount;
            System.out.println("\r\n《" + fileName + "》处理完成：原有初审" + existCount + "条，本次标记" + markCount + "条。");
            ExcelOptRecord record = new ExcelOptRecord();
            record.setFileName(fileName);
            record.setOptTime(new Date());
            record.setTotalCount(sheet.getLastRowNum());
            record.setExistFirstTrialCount(existCount);
            record.setMarkFirstTrialCount(markCount);
            excelOptRecordMapper.insert(record);
        }
        System.out.println("【全部成功】原有初审共计" + totalExistCount + "条，本次标记共计" + totalProcCount + "条。");
        return fileNames;
    }

    /**
     * 多张程处理EXCEL表格
     */
    @Transactional
    public List<String> optExcelWithMultiThred(File srcDir, File tarDir) throws IOException {
        File[] files = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < (files != null ? files.length : 0); i++) {
            String fileName = files[i].getName();
            System.out.println("==========> 正在处理第" + (i + 1) + "个文档，共" + files.length + "个：【" + fileName + "】<==========");
            FileInputStream in = new FileInputStream(files[i]);
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            in.close();
            // 首行为标题行; 第C列为注册号，H列为公告状态
            XSSFSheet sheet = workbook.getSheetAt(0);
            int count = 0;
            System.out.println("待处理的数据共有【" + sheet.getLastRowNum() + "】条，开始处理……");
            boolean newLine = true;
            int dotCount = 0;
            List<Future<Integer>> futures = new ArrayList<>();
            List<XSSFRow> rows0 = new ArrayList<>();
            List<XSSFRow> rows1 = new ArrayList<>();
            List<XSSFRow> rows2 = new ArrayList<>();
            List<XSSFRow> rows3 = new ArrayList<>();
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                switch (j % 4) {
                    case 0:
                        rows0.add(row);
                        break;
                    case 1:
                        rows1.add(row);
                        break;
                    case 2:
                        rows2.add(row);
                        break;
                    case 3:
                        rows3.add(row);
                        break;
                    default:
                        break;
                }
            }
            futures.add(operation(rows0));
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workbook.write(out);
            out.close();
            fileNames.add(fileName);
            if (newLine) {
                System.out.println();
            }
            System.out.println("==========> 【" + fileName + "】处理完成，共处理数据" + count + "条  <==========");
        }
        return fileNames;
    }

    private Future<Integer> operation(List<XSSFRow> rows0) {
        Callable<Integer> thread = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        };
        return null;
    }

    /**
     * 保存公告
     */
    @Transactional
    public int save(Announcement ann) {
        return announcementMapper.insert(ann);
    }

    /**
     * 批量保存公告
     */
    @Transactional
    public int batchSave(List<Announcement> annList) {
        return announcementMapper.insertList(annList);
    }

    /**
     * 根据注册号查询公告信息
     */
    public List<Announcement> getByRegNum(String regNum) {
        return announcementMapper.getByRegNum(regNum);
    }

    /**
     * 根据注册号查询公告数量
     */
    public Integer getCountByRegNum(String regNum) {
        return announcementMapper.getCountByRegNum(regNum);
    }

}
