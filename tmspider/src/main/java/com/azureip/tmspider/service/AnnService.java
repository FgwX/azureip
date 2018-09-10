package com.azureip.tmspider.service;

import com.alibaba.druid.util.StringUtils;
import com.azureip.tmspider.mapper.AnnouncementMapper;
import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.pojo.AnnListPojo;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class AnnService {

    @Autowired
    private AnnouncementMapper mapper;

//    @Autowired
//    private ThreadPoolTaskExecutor executor;

    private static Gson gson = new Gson();
    // RequestHeader: User-Agent
    private static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

    /**
     * 查询本地公告数据库中最新一期数据量
     */
    public Map<String,String> queryLocalLatest(){
        Map<String,String> result = new HashMap<>();
        mapper.queryLocalLatest();
        return result;
    }

    /**
     * 多条件查询公告总数
     */
    public int queryAnnCount(AnnQueryPojo pojo) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        StringBuilder countUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
        countUrl.append("?page=1&rows=1")
                .append("&annNum=" + pojo.getAnnNum())
                .append("&annType=" + pojo.getAnnType())
                .append("&totalYOrN=true");
        if (!StringUtils.isEmpty(pojo.getAppDateBegin()) && !StringUtils.isEmpty(pojo.getAppDateEnd())) {
            countUrl.append("&appDateBegin=" + pojo.getAppDateBegin())
                    .append("&appDateEnd=" + pojo.getAppDateEnd());
        }
        System.out.println("总量查询URL：" + countUrl.toString());
        HttpPost countPost = new HttpPost(countUrl.toString());
        countPost.setHeader("User-Agent", AGENT);
        countPost.setConfig(config);
        CloseableHttpResponse countResp = client.execute(countPost);
        int statusCode = countResp.getStatusLine().getStatusCode();
        AnnListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnListPojo.class);
        client.close();
        return countPojo.getTotal();
    }

    /**
     * 导入公告数据
     */
    @Transactional
    public int importAnns(AnnQueryPojo queryPojo) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        if (queryPojo.getTotal() < 1) {
            StringBuilder countUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            countUrl.append("?page=1&rows=1")
                    .append("&annNum=" + queryPojo.getAnnNum())
                    .append("&annType=" + queryPojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(queryPojo.getAppDateBegin()) && !StringUtils.isEmpty(queryPojo.getAppDateEnd())) {
                countUrl.append("&appDateBegin=" + queryPojo.getAppDateBegin())
                        .append("&appDateEnd=" + queryPojo.getAppDateEnd());
            }
            System.out.println("总量查询URL：" + countUrl.toString());
            HttpPost countPost = new HttpPost(countUrl.toString());
            countPost.setHeader("User-Agent", AGENT);
            countPost.setConfig(config);
            CloseableHttpResponse countResp = client.execute(countPost);
            AnnListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnListPojo.class);
            queryPojo.setTotal(countPojo.getTotal());
        }
        int times = (int) Math.ceil(queryPojo.getTotal() / Double.valueOf(queryPojo.getRows()));
        int successCount = 0;
        for (int i = 0; i < times; i++) {
            StringBuilder listUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            listUrl.append("?page=" + (i + 1))
                    .append("&rows=" + queryPojo.getRows())
                    .append("&annNum=" + queryPojo.getAnnNum())
                    .append("&annType=" + queryPojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(queryPojo.getAppDateBegin()) && !StringUtils.isEmpty(queryPojo.getAppDateEnd())) {
                listUrl.append("&appDateBegin=" + queryPojo.getAppDateBegin())
                        .append("&appDateEnd=" + queryPojo.getAppDateEnd());
            }
            System.out.println("第" + (i + 1) + "次请求URL：" + listUrl.toString());
            HttpPost post = new HttpPost(listUrl.toString());
            post.setHeader("User-Agent", AGENT);
            post.setHeader("Connection", "keep-alive");
            post.setConfig(config);
            long listQureyStart = System.currentTimeMillis();
            CloseableHttpResponse resp = client.execute(post);
            long listQueryEnd = System.currentTimeMillis();
            System.out.println("查询耗时：" + (listQueryEnd - listQureyStart) + "毫秒");
            AnnListPojo annList = gson.fromJson(EntityUtils.toString(resp.getEntity()), AnnListPojo.class);
            long insertStart = System.currentTimeMillis();
            int result = 0;
            try {
                result = batchSave(annList.getRows());
            } catch (Exception e) {
                System.err.println("数据库异常：" + e.getMessage());
                return 0;
            }
            long insertEnd = System.currentTimeMillis();
            System.out.println("插入" + result + "条数据耗时：" + (insertEnd - insertStart) + "毫秒");
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
        int totalCount = 0;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            System.out.println("==========> 正在处理第" + (i + 1) + "个文档，共" + files.length + "个：【" + fileName + "】<==========");
            FileInputStream in = new FileInputStream(files[i]);
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            in.close();
            // 首行为标题行; 第B列为注册号，G列为公告状态
            XSSFSheet sheet = workbook.getSheetAt(0);
            int count = 0;
            System.out.println("待处理的数据共有【" + sheet.getLastRowNum() + "】条，开始处理……");
            boolean newLine = true;
            int dotCount = 0;
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                try {
                    String regNum = row.getCell(1).getStringCellValue();
                    //long queryStart = System.currentTimeMillis();
                    int annCount = getCountByRegNum(regNum);
                    //long queryEnd = System.currentTimeMillis();
                    //System.out.println("查询第" + j + "行耗时" + (queryEnd - queryStart) + "毫秒");
                    if (annCount > 0) {
                        if (newLine) {
                            System.out.println();
                        }
                        row.createCell(6).setCellValue("初审公告");
                        count += 1;
                        System.out.println("正在处理第【" + j + "】行，注册号[" + regNum + "]，查询到" + annCount + "条初审公告");
                        newLine = false;
                    } else {
                        if (!newLine) {
                            dotCount = 0;
                        }
                        if (dotCount >= 100) {
                            System.out.println();
                            dotCount = 0;
                        }
                        System.out.print(".");
                        dotCount++;
                        newLine = true;
                    }
                } catch (NullPointerException e) {
                    System.err.println("正在处理第【" + j + "】行，此行为空！");
                }
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            workbook.write(out);
            out.close();
            fileNames.add(fileName);
            if (newLine) {
                System.out.println();
            }
            totalCount += count;
            System.out.println("==========> 【" + fileName + "】处理完成，共处理数据" + count + "条  <==========");
        }
        System.out.println("==========> 总计共处理初审数据" + totalCount + "条  <==========");
        return fileNames;
    }

    /**
     * 多张程处理EXCEL表格
     */
    @Transactional
    public List<String> optExcelWithMultiThred(File srcDir, File tarDir) throws IOException {
        File[] files = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
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
                switch (j%4){
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
        return mapper.insert(ann);
    }

    /**
     * 批量保存公告
     */
    @Transactional
    public int batchSave(List<Announcement> annList) {
        return mapper.insertList(annList);
    }

    /**
     * 根据注册号查询公告信息
     */
    public List<Announcement> getByRegNum(String regNum) {
        return mapper.getByRegNum(regNum);
    }

    /**
     * 根据注册号查询公告数量
     */
    public Integer getCountByRegNum (String regNum){
        return mapper.getCountByRegNum(regNum);
    }

}
