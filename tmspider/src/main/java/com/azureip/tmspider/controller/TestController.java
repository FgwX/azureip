package com.azureip.tmspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("test")
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static void main(String[] args) throws Exception {
        // 获取EXCEL单元格字体
        /*FileInputStream in = new FileInputStream(new File("D:/TMSpider/test.xlsx"));
        XSSFWorkbook workBook = new XSSFWorkbook(in);
        in.close();
        final XSSFSheet sheet = workBook.getSheetAt(0);
        final XSSFCreationHelper creationHelper = workBook.getCreationHelper();
        final XSSFRow row = sheet.getRow(1);
        final XSSFCell cell = row.getCell(6);
        final XSSFCell cell1 = row.createCell(6);
        ExcelUtil.setText(workBook,cell1,"test");
        final FileOutputStream op = new FileOutputStream(new File("D:/TMSpider/target.xlsx"));
        workBook.write(op);
        op.close();*/

        // 死循环
        /*int random = 0;
        while (random<100) {
            random = random * 10;
        }*/

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
