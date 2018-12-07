package com.azureip.tmspider.controller;

import com.alibaba.druid.util.StringUtils;
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

@Controller
@RequestMapping("ann")
public class AnnController {

    private final AnnService annService;

    @Autowired
    public AnnController(AnnService annService) {
        this.annService = annService;
    }

    /**
     * 查询本地公告中最新一期的期号
     */
    @GetMapping("queryLocalLatestAnnNum")
    @ResponseBody
    public GlobalResponse<String> queryLocalLatestAnnNum() {
        GlobalResponse<String> response = new GlobalResponse<>();
        String annNum = annService.queryLocalLatestAnnNum();
        if (!StringUtils.isEmpty(annNum)) {
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResult(annNum);
        } else {
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage("没有查询到公告！");
        }
        return response;
    }

    /**
     * 查询公告总数 - 前端测试方法
     */
    @PostMapping("queryAnnCountTest")
    @ResponseBody
    public GlobalResponse<Integer> queryAnnCountTest(AnnQueryPojo pojo) {
        GlobalResponse<Integer> response = new GlobalResponse<>();
        response.setStatus(GlobalResponse.SUCCESS);
        response.setResultList(annService.queryAnnCountTest(pojo));
        return response;
    }

    /**
     * 查询公告总数
     */
    @PostMapping("queryAnnCount")
    @ResponseBody
    public GlobalResponse<Integer> queryAnnCount(AnnQueryPojo pojo) {
        GlobalResponse<Integer> response = new GlobalResponse<>();
        try {
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResultList(annService.queryAnnCount(pojo));
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
        GlobalResponse<Integer> response = new GlobalResponse<>();
        try {
            int successCount = annService.importAnns(pojo);
            if (successCount > 0) {
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
