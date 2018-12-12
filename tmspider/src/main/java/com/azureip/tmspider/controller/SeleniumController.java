package com.azureip.tmspider.controller;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumController {

    public static void main(String[] args) {
        // 设置ChromeDriver服务地址
        System.setProperty("webdriver.chrome.driver", "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\chromedriver.exe");
        // ChromeOptions
        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1200, 700));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
        // Set<Cookie> cookies = driver.manage().getCookies();
        // driver.get("http://sbj.saic.gov.cn/sbcx/");
        // driver.manage().deleteAllCookies();
        driver.get("http://wsjs.saic.gov.cn/txnT01.do?y7bRbp=qmMIygQ8vhgn50WEiiZHNtbW9Bv82J7Uh13.Q.0f17uz8n0BqCXc4Q4ZvWVP647wwVSySJ5wsj3vbcLD9ERzaZwBuCTEMPd9bnFHEUf9VrgM0Cpx5zJDScvxLIL_D9rPLstMaM7auXJd5yjGgzXD6FP2J3D0p2cD0d9XZVSPPNSQLEkL");

    }
}
