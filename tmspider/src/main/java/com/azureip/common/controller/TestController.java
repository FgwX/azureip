package com.azureip.common.controller;

import com.azureip.common.util.ExcelUtils;
import com.azureip.common.util.JSUtils;
import com.azureip.common.util.SeleniumUtils;
import com.azureip.tmspider.pojo.AnnListPojo;
import com.azureip.tmspider.service.RegistrationService;
import com.eclipsesource.v8.V8;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("")
public class TestController {
    static {
        String projectBase = TestController.class.getClassLoader().getResource("").getPath();
        CHROME_DRIVER_DIR = projectBase + "drivers/chromedriver.exe";
        FF_DRIVER_DIR = projectBase + "drivers/geckodriver.exe";
    }

    private static final String CHROME_DRIVER_DIR;
    private static final String FF_DRIVER_DIR;

    @GetMapping("test")
    public String test() {
        System.err.println(">>>> Entering test method... <<<<");
        return "forward:index.html";
    }

    public static void main(String[] args) throws Exception {
        // commandLineTest();
        // setProxy("218.73.58.18","14551");
        // removeProxy();
        firefoxProxyTest();
        // jsReadyStateTest();
        // crackAnnPost();
        // gsonTest();
        // dynamicLoadConfig();
        // javaMailTest();
        // seleniumTest();
        // getExcelUnitFont();
        // deadLoop();
        // getAbsoluteFilePath();
        // getFirstDayOfMonth();
        // getRandomNum();
    }

    private static void commandLineTest() {
        BufferedReader reader = null;
        try {
            Process proc = Runtime.getRuntime().exec("ipconfig");
            reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("执行结果：" + proc.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Chrome代理测试
    private static void chromeProxyTest() {
        // 代理服务器
        // 118.89.234.236:8787
        // 119.23.21.39:80
        String[] ip = "117.78.33.45:80".split(":");
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);

        Proxy proxy = new Proxy();

        ChromeOptions o = new ChromeOptions();
        o.setProxy(proxy);
        FirefoxProfile profile = new FirefoxProfile();
        // FirefoxProfile profile = new ProfilesIni().getProfile("default");
        // 使用代理
        profile.setPreference("network.ip.type", 1);
        // 代理服务器配置
        profile.setPreference("network.ip.http", ip[0]);
        profile.setPreference("network.ip.http_port", ip[1]);

        profile.setPreference("network.ip.ssl", ip[0]);
        profile.setPreference("network.ip.ssl_port", ip[1]);

        // profile.setPreference("username", proxyUser);
        // profile.setPreference("password", proxyPass);

        // 所有协议公用一种代理配置，如果单独配置，这项设置为false
        profile.setPreference("network.ip.share_proxy_settings", true);

        // 对于localhost的不用代理，这里必须要配置，否则无法和webdriver通讯
        profile.setPreference("network.ip.no_proxies_on", "localhost");
        FirefoxOptions option = new FirefoxOptions();
        option.setProfile(profile);
        // 以代理方式启动firefox
        FirefoxDriver driver = new FirefoxDriver(option);

        driver.get("http://ip138.com");
        // driver.get("http://wsjs.saic.gov.cn");
    }

    // Firefox代理测试
    private static void firefoxProxyTest() {
        // 代理服务器
        final String proxyHost = "52.80.58.248";
        final int proxyPort = 3128;
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);

        FirefoxProfile profile = new FirefoxProfile();
        // FirefoxProfile profile = new ProfilesIni().getProfile("default");
        // 使用代理
        profile.setPreference("network.proxy.type", 1);
        // 代理服务器配置
        profile.setPreference("network.proxy.http", proxy[0]);
        profile.setPreference("network.proxy.http_port", proxy[1]);

        profile.setPreference("network.proxy.ssl", proxy[0]);
        profile.setPreference("network.proxy.ssl_port", proxy[1]);

        // profile.setPreference("username", proxyUser);
        // profile.setPreference("password", proxyPass);

        // 所有协议公用一种代理配置，如果单独配置，这项设置为false
        profile.setPreference("network.proxy.share_proxy_settings", true);

        // 对于localhost的不用代理，这里必须要配置，否则无法和webdriver通讯
        profile.setPreference("network.proxy.no_proxies_on", "localhost");
        FirefoxOptions option = new FirefoxOptions();
        option.setProfile(profile);
        // 以代理方式启动firefox
        WebDriver driver = new FirefoxDriver(option);

        driver.get("http://www.azure-ip.com");

    }

    private static void jsReadyStateTest() {
        // 初始化Selenium功能参数
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_DIR);
        System.setProperty("webdriver.gecko.driver", FF_DRIVER_DIR);
        WebDriver driver = SeleniumUtils.initBrowser(false, null);

        driver.get("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html?page=3&rows=100&annNum=1638&annType=TMZCSQ&totalYOrN=true&agentName=");
        String source = driver.getPageSource();
        String orgJS = source.substring(source.indexOf("<script>") + "<script>".length(), source.indexOf("</script>"));
        System.out.println(orgJS);

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogEntries logEntries = driver.manage().logs().get("performance");
            Iterator<LogEntry> iterator = logEntries.iterator();
            while (iterator.hasNext()) {
                LogEntry log = iterator.next();
                JSONObject message = new JSONObject(log.getMessage()).getJSONObject("message");
                JSONObject params = message.getJSONObject("params");
                System.out.println(params);
            }
            Object readyState = jsExecutor.executeScript("return document.readyState");
            Object status = jsExecutor.executeScript("return document.visibilityState");
            System.out.println("readyState: " + readyState);
            System.out.println("status: " + status);
        }
    }

    private static void crackAnnPost() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        String countUrl = "http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html?page=1&rows=1&annNum=1636&annType=TMZCSQ&totalYOrN=true&agentName=";
        HttpPost countPost = new HttpPost(countUrl);
        countPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        countPost.setConfig(config);
        HttpResponse response = JSUtils.crackAnnPost(client, V8.createV8Runtime(), countPost);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    private static void gsonTest() {
        Gson gson = new Gson();
        AnnListPojo pojo = gson.fromJson("{\"total\":101200,\"rows\":[]}", AnnListPojo.class);
        System.out.println(pojo.getTotal());
        System.out.println(pojo.getRows());
    }

    private static void dynamicLoadConfig() {
        try {
            Properties prop = new Properties();
            String path = Thread.currentThread().getContextClassLoader().getResource("test.properties").getPath();
            System.out.println(path);
            FileInputStream in = new FileInputStream(path);
            prop.load(in);
            for (int i = 0; i < 100; i++) {
                System.err.println(prop.getProperty("a"));
                Thread.sleep(1000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // JavaMail测试
    private static void javaMailTest() {
        try {

            // 设置连接邮件服务器的参数
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.setProperty("mail.smtp.host", "smtp.azure-ip.com");
            // 创建定义整个应用程序所需的环境信息的Session对象
            Session session = Session.getInstance(properties);
            session.setDebug(true);
            Message msg = new MimeMessage(session);
            // 设置发件人地址
            msg.setFrom(new InternetAddress("lewiszhang@azure-ip.com"));
            // 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
            // MimeMessage.RecipientType.TO：发送；CC：抄送；BCC：密送
            msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("azure-cs01@azure-ip.com"));
            // 设置邮件主题
            msg.setSubject("测试邮件主题");
            // 设置邮件正文
            msg.setContent("简单的纯文本邮件！", "text/html;charset=UTF-8");
            // 设置邮件的发送时间,默认立即发送
            msg.setSentDate(new Date());
            // 根据session对象获取邮件传输对象Transport
            Transport transport = session.getTransport();
            // 设置发件人的账户名和密码
            transport.connect("lewiszhang@azure-ip.com", "Cqwy860328");
            // 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(msg, msg.getAllRecipients());
            // 如果只想发送给指定的人，可以如下写法
            // transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});
            // 关闭邮件连接
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Selenium测试 - Chrome启动参数
    private static void seleniumTest() {
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
        // ChromeDriver driver = new ChromeDriver();
        FirefoxDriver driver = new FirefoxDriver();
        driver.get("http://service.spiritsoft.cn/ua.html");
    }

    // 获取EXCEL单元格字体
    private static void getExcelUnitFont() {
        try {
            FileInputStream in = new FileInputStream(new File("D:/TMSpider/test.xlsx"));
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();
            final XSSFSheet sheet = workBook.getSheetAt(0);
            final XSSFCreationHelper creationHelper = workBook.getCreationHelper();
            final XSSFRow row = sheet.getRow(1);
            final XSSFCell cell = row.getCell(6);
            final XSSFCell cell1 = row.createCell(6);
            ExcelUtils.setText(workBook, cell1, "test");
            final FileOutputStream op = new FileOutputStream(new File("D:/TMSpider/target.xlsx"));
            workBook.write(op);
            op.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 死循环
    private static void deadLoop() {
        int random = 0;
        while (random < 100) {
            random = random * 10;
        }
    }

    // 获取文件绝对路径
    private static void getAbsoluteFilePath() {
        final URL url = TestController.class.getClassLoader().getResource("");
        System.out.println(url.getPath());
    }

    // 获取当月第一天
    private static void getFirstDayOfMonth() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDayOfCurrentMonth = sdf.parse(sdf.format(calendar.getTime()));
            System.out.println(sdf2.format(firstDayOfCurrentMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 获取随机数
    private static void getRandomNum() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(10));
        }
    }

    public static boolean setProxy(String ip, String port) {
        String code = "@echo off\r\n"
                + "set ip=" + ip + "\r\n"
                + "set port=" + port + "\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\\Connections\" /v \"DefaultConnectionSettings\" /t  REG_BINARY /d \"3C000000AA0100000B0000000F000000\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyEnable\" /t  REG_DWORD /d \"1\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyServer\" /t  REG_SZ /d \"%ip%:%port%\" /f\r\n"
                + "REG ADD \"HKLM\\System\\CurrentControlSet\\Hardware Profiles\\0001\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyEnable\" /t  REG_DWORD /d \"1\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\\Connections\" /v \"SavedLegacySettings\" /t  REG_BINARY /d \"3C000000AE0100000B0000000F000000\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyOverride\" /t  REG_SZ /d \"<local>\" /f\r\n"
                + "set rp=\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\"\r\n"
                + "set rk=\"ProxyServer\"\r\n"
                + "for /f \"tokens=*\" %%a in ('reg query %rp% /v %rk%^|findstr %rk%') do (\r\n"
                + "call :doit %%a\r\n"
                + ")\r\n"
                + "goto :eof\r\n"
                + ":doit\r\n"
                + "echo %3\r\n"
                + "echo.&echo.\r\n"
                + "exit";
        return runBat(code, ip + ":" + port);
    }

    public static void removeProxy() {
        String code = "@echo off\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\\Connections\" /v \"DefaultConnectionSettings\" /t  REG_BINARY /d \"3C000000AA0100000B0000000F000000\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyEnable\" /t  REG_DWORD /d \"0\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyServer\" /t  REG_SZ /d \"\" /f\r\n"
                + "REG ADD \"HKLM\\System\\CurrentControlSet\\Hardware Profiles\\0001\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyEnable\" /t  REG_DWORD /d \"1\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\\Connections\" /v \"SavedLegacySettings\" /t  REG_BINARY /d \"3C000000AE0100000B0000000F000000\" /f\r\n"
                + "REG ADD \"HKCU\\SOFTWARE\\MICROSOFT\\Windows\\CURRENTVERSION\\Internet Settings\" /v \"ProxyOverride\" /t  REG_SZ /d \"<local>\" /f\r\n"
                + "exit";
        runBat(code, "");
    }

    private static boolean runBat(String code, String flag) {
        File file = new File("temp.bat");
        String str = "";
        try {
            if (!file.exists())
                file.createNewFile();
            PrintWriter pw = new PrintWriter(file);
            pw.write(code);
            pw.flush();
            pw.close();
            Process child = Runtime.getRuntime().exec("cmd /c temp.bat");
            InputStream in = child.getInputStream();
            Scanner sc = new Scanner(in, "gbk");
            while (sc.hasNext()) {
                str += sc.nextLine();
            }
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sc.close();
            Thread.sleep(1000);
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (str.contains(flag))
            return true;
        else
            return false;
    }
}
