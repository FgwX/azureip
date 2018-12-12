package com.azureip.tmspider.controller;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("reg")
public class RegistrationController {

    @GetMapping("test")
    public void test() throws IOException {
        FileInputStream in = new FileInputStream("D:\\test.xlsx");
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

}
