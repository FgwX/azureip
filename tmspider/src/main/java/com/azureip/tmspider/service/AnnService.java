package com.azureip.tmspider.service;

import com.alibaba.druid.util.StringUtils;
import com.azureip.tmspider.mapper.AnnouncementMapper;
import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.azureip.tmspider.pojo.AnnoucementListPojo;
import com.azureip.tmspider.pojo.AnnoucementPojo;
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

import java.io.*;
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
        System.out.println("查询总数URL：" + countUrl.toString());
        HttpPost countPost = new HttpPost(countUrl.toString());
        countPost.setHeader("User-Agent", AGENT);
        countPost.setConfig(config);
        CloseableHttpResponse countResp = client.execute(countPost);
        AnnoucementListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnoucementListPojo.class);
        client.close();
        return countPojo.getTotal();
    }

    /**
     * 导入公告数据
     */
    public int importAnns(AnnQueryPojo pojo) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        if (pojo.getTotal() < 1){
            StringBuilder countUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            countUrl.append("?page=1&rows=1")
                    .append("&annNum=" + pojo.getAnnNum())
                    .append("&annType=" + pojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(pojo.getAppDateBegin()) && !StringUtils.isEmpty(pojo.getAppDateEnd())) {
                countUrl.append("&appDateBegin=" + pojo.getAppDateBegin())
                        .append("&appDateEnd=" + pojo.getAppDateEnd());
            }
            System.out.println("查询总数URL：" + countUrl.toString());
            HttpPost countPost = new HttpPost(countUrl.toString());
            countPost.setHeader("User-Agent", AGENT);
            countPost.setConfig(config);
            CloseableHttpResponse countResp = client.execute(countPost);
            AnnoucementListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnoucementListPojo.class);
            pojo.setTotal(countPojo.getTotal());
        }
        int times = (int) Math.ceil(pojo.getTotal() / Double.valueOf(pojo.getRows()));
        int successCount = 0;
        for (int i = 0; i < times; i++) {
            StringBuilder listUrl = new StringBuilder("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            listUrl.append("?page=" + (i + 1))
                    .append("&rows=" + pojo.getRows())
                    .append("&annNum=" + pojo.getAnnNum())
                    .append("&annType=" + pojo.getAnnType())
                    .append("&totalYOrN=true");
            if (!StringUtils.isEmpty(pojo.getAppDateBegin()) && !StringUtils.isEmpty(pojo.getAppDateEnd())) {
                listUrl.append("&appDateBegin=" + pojo.getAppDateBegin())
                        .append("&appDateEnd=" + pojo.getAppDateEnd());
            }
            HttpPost post = new HttpPost(listUrl.toString());
            post.setHeader("User-Agent", AGENT);
            post.setHeader("Connection", "keep-alive");
            post.setConfig(config);
            CloseableHttpResponse resp = client.execute(post);
            AnnoucementListPojo annList = gson.fromJson(EntityUtils.toString(resp.getEntity()), AnnoucementListPojo.class);
            for (AnnoucementPojo ann : annList.getRows()) {
                int result = save(ann);
                if (result > 0) {
                    successCount += result;
                    System.out.println("Success: " + ann);
                } else {
                    System.err.println(" Failed: " + ann);
                }
            }
        }
        client.close();
        System.out.println("Total Success: " + successCount);
        return successCount;
    }

    /**
     * 处理EXCEL表格，标注已有初审公告的行
     */
    public List<String> optExcel(File srcDir, File tarDir) throws IOException {
        File[] files = srcDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            System.out.println("==========> 正在处理第" + (i + 1) + "个文档，共" + files.length + "个：【" + fileName + "】<==========");
            FileInputStream in = new FileInputStream(files[i]);
            XSSFWorkbook src = new XSSFWorkbook(in);
            in.close();
            // 首行为标题行; 第C列为注册号，H列为公告状态
            XSSFSheet sheet = src.getSheetAt(0);
            int count = 0;
            System.out.println("待处理的数据共有【" + sheet.getLastRowNum() + "】条，开始处理……");
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                try {
                    String regNum = row.getCell(2).getStringCellValue();
                    List<Announcement> annList = getByRegNum(regNum);
                    if (annList.size() > 0) {
                        row.createCell(7).setCellValue("初审公告");
                        count += 1;
                        System.out.println("正在处理第【" + j + "】行，注册号[" + regNum + "]，查询到" + annList.size() + "条初审公告");
                    }
                } catch (NullPointerException e) {
                    System.err.println("正在处理第【" + j + "】行，此行为空！");
                }
            }
            // 输出目标文件
            FileOutputStream out = new FileOutputStream(tarDir + File.separator + fileName);
            src.write(out);
            out.close();
            fileNames.add(fileName);
            System.out.println("==========> 【" + fileName + "】处理完成，共处理数据" + count + "条  <==========");
        }
        return fileNames;
    }

    /**
     * 保存公告
     */
    public int save(AnnoucementPojo pojo) {
        Announcement ann = new Announcement(
                pojo.getId(),
                pojo.getPage_no(),
                pojo.getAnn_type_code(),
                pojo.getAnn_type(),
                pojo.getAnn_num(),
                pojo.getAnn_date(),
                pojo.getReg_name(),
                pojo.getReg_num(),
                pojo.getTm_name()
        );
        return mapper.insert(ann);
    }

    /**
     * 根据注册号查询公告信息
     */
    public List<Announcement> getByRegNum(String regNum) {
        return mapper.getByRegNum(regNum);
    }

}
