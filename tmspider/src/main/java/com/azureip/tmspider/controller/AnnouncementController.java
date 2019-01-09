package com.azureip.tmspider.controller;

import com.alibaba.druid.util.StringUtils;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.service.AnnouncementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class AnnouncementController {
    // private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AnnouncementController.class);
    private static final Logger LOG = LogManager.getLogger(AnnouncementController.class);

    private final AnnouncementService announcementService;

    @Autowired
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 查询本地公告中最新一期的期号
     */
    @GetMapping("queryLocalLatestAnnNum")
    @ResponseBody
    public GlobalResponse<String> queryLocalLatestAnnNum() {
        GlobalResponse<String> response = new GlobalResponse<>();
        String annNum = announcementService.queryLocalLatestAnnNum();
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
        try {
            System.out.println("queryAnnCountTest begins...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GlobalResponse<Integer> response = new GlobalResponse<>();
        response.setStatus(GlobalResponse.SUCCESS);
        List<Integer> list = new ArrayList<>();
        list.add(1234);
        list.add(2345);
        response.setResultList(list);
        return response;
    }

    /**
     * 查询公告总数
     */
    @PostMapping("queryAnnCount")
    @ResponseBody
    public GlobalResponse<Integer> queryAnnCount(AnnQueryPojo pojo) {
        System.out.println(pojo);
        LOG.info("QueryPojo: " + pojo);
        GlobalResponse<Integer> response = new GlobalResponse<>();
        try {
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResultList(announcementService.queryAnnCount(pojo));
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
            int successCount = announcementService.importAnns(pojo);
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
     * 根据期号删本地公告
     */
    @PostMapping("deleteByAnnNum")
    @ResponseBody
    public GlobalResponse deleteAnnByAnnNum(AnnQueryPojo pojo) {
        try {
            System.out.println("正在删除第" + pojo.getAnnNum() + "期公告...");
            announcementService.deleteAnnByAnnNum(pojo.getAnnNum());
            return new GlobalResponse(GlobalResponse.SUCCESS, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new GlobalResponse(GlobalResponse.ERROR, "删除失败");
        }
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
            List<String> fileNames = announcementService.optExcel(srcDir, tarDir);
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
