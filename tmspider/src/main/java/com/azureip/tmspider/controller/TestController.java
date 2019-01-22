package com.azureip.tmspider.controller;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Random;

@Controller
@RequestMapping("test")
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static void main(String[] args) throws ParseException, IOException {
        FileInputStream in = new FileInputStream(new File("D:/TMSpider/test.xlsx"));
        XSSFWorkbook workBook = new XSSFWorkbook(in);
        in.close();
        XSSFCell cell = workBook.getSheetAt(0).getRow(1).getCell(6);
        // System.out.println(cell.getStringCellValue());

        // System.out.println(cell.getNumericCellValue());
        System.out.println(cell.getCellTypeEnum().toString());
        // System.out.println(CellType.BOOLEAN.compareTo(cell.getCellTypeEnum()));

        // 获取文件绝对路径
        /*final URL url = TestController.class.getClassLoader().getResource("");
        System.out.println(url.getPath());*/

        // 获取当月第一天
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfCurrentMonth = sdf.parse(sdf.format(calendar.getTime()));
        System.out.println(sdf2.format(firstDayOfCurrentMonth));*/
        // 获取随机数
        /*Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(10));
        }*/
    }
}
