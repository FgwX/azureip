package com.azureip.tmspider.controller;

import com.azureip.common.pojo.GlobalResponse;
import com.azureip.tmspider.service.RejectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("rej")
public class RejectionController {
    private final RejectionService rejectionService;

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
        File srcDir = new File("D:/TMSpider/rej_src");
        File tarDir = new File("D:/TMSpider/rej_tar");
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
