package com.azureip.tmspider.controller;

import com.azureip.tmspider.service.RegistrationService;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
        String CHROME_DRIVER_DIR;
        String FF_DRIVER_DIR;
        String projectBase = RegistrationService.class.getClassLoader().getResource("").getPath();
        CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_DIR);
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-background-networking",
                "--disable-client-side-phishing-detection",
                "--disable-default-apps",
                "--disable-hang-monitor",
                "--disable-popup-blocking",
                "--disable-prompt-on-repost",
                "--disable-sync",
                "--disable-web-resources",
                "--enable-automation",
                "--enable-logging",
                "--force-fieldtrials=SiteIsolationExtensions/Control",
                "--ignore-certificate-errors",
                "--load-extension=C:/Users/fgwx/AppData/Local/Temp/scoped_dir15384_8149/internal",
                "--log-level=0",
                "--metrics-recording-only",
                "--no-first-run",
                "--password-store=basic",
                "--remote-debugging-port=0",
                "--test-type=webdriver",
                "--use-mock-keychain",
                "--user-data-dir=C:/Users/fgwx/AppData/Local/Google/Chrome/User Data",
                "--flag-switches-begin",
                "--flag-switches-end data:");

        // options.addArguments("--no-sandbox","--user-data-dir=C:/Users/fgwx/AppData/Local/Google/Chrome/User Data","blink-settings=imagesEnabled=false");
        // DesiredCapabilities cap = DesiredCapabilities.chrome();
        // cap.setCapability(ChromeOptions.CAPABILITY, options);
        // LoggingPreferences logPref = new LoggingPreferences();
        // logPref.enable(LogType.PERFORMANCE, Level.ALL);
        // cap.setCapability(CapabilityType.LOGGING_PREFS, logPref);
        // options.setCapability(ChromeOptions.CAPABILITY, cap);
        ChromeDriver driver = new ChromeDriver(options);

        driver.get("chrome://version");

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
