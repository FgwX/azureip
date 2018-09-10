package com.azureip.tmspider.controller;

import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.service.AnnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("ann")
public class AnnController {

    @Autowired
    private AnnService annService;

    /**
     * 查询本地公告数据库中最新一期数据量
     */
    @GetMapping("queryLocalLatest")
    @ResponseBody
    public GlobalResponse<Map<String,String>> queryLocalLatest(){

        return null;
    }

    /**
     * 查询公告总数
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
     */
    @PostMapping("importAnns")
    @ResponseBody
    public GlobalResponse<Integer> importAnns(AnnQueryPojo pojo) {
        GlobalResponse<Integer> response = new GlobalResponse<Integer>();
        try {
            int successCount = annService.importAnns(pojo);
            if (successCount > 0){
                response.setStatus(GlobalResponse.SUCCESS);
                response.setResult(successCount);
            } else {
                response.setStatus(GlobalResponse.ERROR);
                response.setMessage("数据导入失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 处理EXCEL表格，标记出已有初审公告的行
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
}
