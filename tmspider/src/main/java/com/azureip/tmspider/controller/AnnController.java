package com.azureip.tmspider.controller;

import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.azureip.tmspider.pojo.AnnoucementListPojo;
import com.azureip.tmspider.pojo.AnnoucementPojo;
import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.service.AnnService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("ann")
public class AnnController {

    @Autowired
    private AnnService annService;

    /**
     * 查询公告总数
     *
     * @return
     */
    @PostMapping("queryAnnCount")
    @ResponseBody
    public GlobalResponse<Integer> queryAnnCount(AnnQueryPojo pojo) {
        GlobalResponse<Integer> response = new GlobalResponse<Integer>();
        try {
            int count = annService.queryAnnCount(pojo);
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResult(count);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 查询导入公告数据
     *
     * @param pojo
     * @return
     */
    @PostMapping("importAnns")
    @ResponseBody
    public GlobalResponse<Integer> importAnns(AnnQueryPojo pojo) {
        GlobalResponse<Integer> response = new GlobalResponse<Integer>();
        try {
            int successCount = annService.importAnns(pojo);
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResult(successCount);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 处理EXCEL表格，标记出已有初审公告的行。
     *
     * @return
     * @throws IOException
     */
    @GetMapping("optExcel")
    @ResponseBody
    public GlobalResponse<String> optExcel() {
        GlobalResponse<String> response = new GlobalResponse<>();
        // 获取待处理的EXCEL文件集合
        File srcDir = new File("D:\\TMSpider\\src");
        File tarDir = new File("D:\\TMSpider\\tar");
        try {
            List<String> fileNames = annService.optExcel(srcDir, tarDir);
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResultList(fileNames);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public static void main(String[] args) {
        AnnQueryPojo pojo = new AnnQueryPojo();
        int total = pojo.getTotal();
        System.out.println("Total: " + total);
    }
}
