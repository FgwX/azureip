package com.azureip.tmspider.controller;

import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.service.RegistrationService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("reg")
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("test")
    public void test() throws IOException {
        FileInputStream in = new FileInputStream("D:/test.xlsx");
        XSSFWorkbook excel = new XSSFWorkbook(in);
        in.close();
        XSSFSheet sheet = excel.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(3);
            System.out.println(cell.getStringCellValue());
            System.out.println(cell.getHyperlink().getAddress());
        }
    }

    // 测试URL: http://localhost/reg/optRej
    @GetMapping("optRej")
    public GlobalResponse optRejections() {
        GlobalResponse<String> response = new GlobalResponse<>();
        // 获取待处理的EXCEL文件夹
        File tarDir = new File("D:/TMSpider/mark_ann");
        try {
            List<String> fileNames = registrationService.optRejections(tarDir);
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
