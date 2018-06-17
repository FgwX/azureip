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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnService {

    @Autowired
    private AnnouncementMapper mapper;
    private static Gson gson = new Gson();
    // RequestHeader: User-Agent
    private static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

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
            int result = batchSave(annList.getRows());
            long insertEnd = System.currentTimeMillis();
            System.out.println("插入" + result + "条数据耗时：" + (insertEnd - insertStart) + "毫秒");
            successCount += result;
            /*List<Announcement> batchList = new ArrayList<>();
            for (Announcement ann : annList.getRows()) {
                batchList.add(ann);
                if (batchList.size() >= 10000) {
                    long insertStart = System.currentTimeMillis();
                    int result = batchSave(batchList);
                    long insertEnd = System.currentTimeMillis();
                    System.out.println("插入" + result + "条数据耗时：" + (insertEnd - insertStart) + "毫秒");
                    successCount += result;
                    batchList.clear();
                }
            }
            if (!batchList.isEmpty()) {
                int result = batchSave(batchList);
                successCount += result;
                batchList.clear();
            }*/
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
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                try {
                    String regNum = row.getCell(2).getStringCellValue();
                    List<Announcement> annList = getByRegNum(regNum);
                    if (annList.size() > 0) {
                        if (newLine) {
                            System.out.println();
                        }
                        row.createCell(7).setCellValue("初审公告");
                        count += 1;
                        System.out.println("正在处理第【" + j + "】行，注册号[" + regNum + "]，查询到" + annList.size() + "条初审公告");
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
            System.out.println("==========> 【" + fileName + "】处理完成，共处理数据" + count + "条  <==========");
        }
        return fileNames;
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

}
