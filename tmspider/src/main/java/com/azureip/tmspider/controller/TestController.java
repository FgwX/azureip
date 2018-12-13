package com.azureip.tmspider.controller;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("test")
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static strictfp void main(String[] args) {
        String[] links = {
                "http://wsjs.saic.gov.cn/txnDetail2.do?y7bRbp=qmM8IcrSNhXZP_jcvWwohC4ez9SWqGR84SNo8t1SO5tt3TTRYEhitygZNqY8Zf2jS8EEpXTcYp.G30FPvoprJMLxgXt2SEsolIaiI_TAcvckirmc_RtP6TLI_Gf0x410NKlolDbsMUwa.trqUfIG3rcJMFKixy4QvjuOWsSSYXrTo9qI&c1K5tw0w6_=2wUjjQW7.neps7JI9370AkYiEcfgwfvBIVg34o3Bru.1LKXxXAODhp1YISGdfbnZrSOwRaq2hJlKq_3AW3i6oe6CmeYDsdw_mGu7k4znym3oz.qfCiXSEIJ.dmaI9ROsM4efDCLUNdBvFOH9EGo02MFpSgpnwCHfys1ouQhEBlfmfokfGTbArPBRRPYFulCya5L1b1BLJyTd5grPgcSnvdqgwVYvV4pmV82Vlxrs2s8c3VHYS3XRM5jSr5AIvbe7Q",
                "http://wsjs.saic.gov.cn/txnDetail2.do?y7bRbp=qmFGtyl31lARlWcRshp9vWP78SyGLUz9_f7vuxDIAajPXZ4EgLouEnxnXDdDmjajlWUO6haHrcfKuMckyF.A3dHVFF0SM7LPRV6rGosC0vxMgyoweO1zjcYGh9GbqYDPAnxqrwXitbCUCnSKTLQWvOfD8i1clSW5by63VpWjDedwx0qR&c1K5tw0w6_=27SlnfASnTJYy4jL5a.sOtwY.ZzPD5tNhdel3nTCmsAtvnicAWJeqpGjglOu5TdZUgrpPp.a0XHRrz9spCajs79rUxMh3ZBbQklfhZ9BjnIA_v6uUyjz0Rid.BWNaDx7u.Ko_oQoby20PnitZu6.uTaDNF0_dWDxikyHyq1SmeFtg8iqt3KT5YTrh8B9mcFslLY97k9HLjUwiGSDbiPQmentFstoamFGVU0RHW5I_fJ3xwvMLULsE17Q4bOwewNHe",
                "http://wsjs.saic.gov.cn/txnDetail2.do?y7bRbp=qmFAjb2fry30.42iEROd5psMHkWabz8XfSQSMKU.Wtko.f.R8yRnxlx4aGYL9WNhrWRvEaR4v8PWz_wfjZePWFxo84UCVfoAqxJUCvHeR8_t7bhHTfYvKwOWJReNIfTZpSKGOuj5u1ywH.GCh.UM7FuAtI4zhrYlg_1zQyjVVOemi7qT&c1K5tw0w6_=2gRBSn1r7BfQQf2j4QrJRlEDqTsXQeRp4FjbV9koPUCuJdL1JeJgGMdV.hfXj8lMjw7GYGqdIgk0Bdm9URaA7r7Tvq6CthiyOMY5s8bSFIY.aWSQiimQPGMNU8JpVftxhMELw.Mlc1.cjpWPYYHq.oqfhxL6GcLqM1UcSUwAo0WP9FjZgLkgcDb2fbw5k_UyvstkouxgEGc.vqPJw.kNdC5N_VT0HIniYp4Yi1LBo4.8H.EoGyno5_b3tNx.fqRwq"};
        String firefoxDriverDir = "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\geckodriver.exe";
        String firefoxBinDir = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
        System.setProperty("webdriver.firefox.bin", firefoxBinDir);
        System.setProperty("webdriver.gecko.driver", firefoxDriverDir);

        FirefoxDriver driver = new FirefoxDriver();
        // for (String link : links) {
        driver.get(links[1]);
        // 设置等待方式及时间
        WebDriverWait wait = new WebDriverWait(driver, 5, 500);
        List<WebElement> infos = wait.until(new ExpectedCondition<List<WebElement>>() {
            @NullableDecl
            @Override
            public List<WebElement> apply(@NullableDecl WebDriver webDriver) {
                return webDriver.findElements(By.cssSelector(".lcbg > ul:nth-child(3) > li:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(3) > span:nth-child(1)"));
            }
        });
        System.out.println("===> Load success");
        wait(5000);
        for (WebElement info : infos) {
            System.out.println(info.getText());
        }
        // }
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
