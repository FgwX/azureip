package com.azureip.tmspider.controller;

import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.service.RejectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("rej")
public class RejectionController {
    final RejectionService rejectionService;

    @Autowired
    public RejectionController(RejectionService rejectionService) {
        this.rejectionService = rejectionService;
    }

    /**
     * 按表格查询回驳
     * URL: http://localhost/rej/queryRej
     */
    @GetMapping("queryRej")
    public GlobalResponse queryRejections() {
        GlobalResponse<String> response = new GlobalResponse<>();
        // 获取待处理的EXCEL文件夹
        File srcDir = new File("D:/TMSpider/src_file");
        File tarDir = new File("D:/TMSpider/mark_rej");
        try {
            List<String> fileNames = rejectionService.queryRejections(srcDir, tarDir);
            response.setStatus(GlobalResponse.SUCCESS);
            response.setResultList(fileNames);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(GlobalResponse.ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}