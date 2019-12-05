package com.azureip.tmspider.controller;

import com.azureip.common.pojo.GlobalResponse;
import com.azureip.tmspider.service.ExtensionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("ext")
public class ExtensionController {
    private static final Logger LOG = LogManager.getLogger(ExtensionController.class);
    private final ExtensionService extensionService;

    @Autowired
    public ExtensionController(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    /**
     * 处理表格（查询续展，添加链接）
     * URL: http://localhost/ext/addLink
     */
    @GetMapping("addLink")
    public GlobalResponse addLink() {
        GlobalResponse<String> response = new GlobalResponse<>();
        // 获取待处理的EXCEL文件夹
        File srcDir = new File("D:/TMSpider/ext_src");
        File tarDir = new File("D:/TMSpider/ext_tar");
        try {
            List<String> fileNames = extensionService.optExtensions(srcDir, tarDir);
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
