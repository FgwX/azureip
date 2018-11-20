package com.azureip.tmspider.controller;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumController {

    public static void main(String[] args) {
        // 设置ChromeDriver服务地址
        System.setProperty("webdriver.chrome.driver", "D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\resources\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://wsjs.saic.gov.cn/txnT01.do");
        // 找到kw元素的id，然后输入hello
        WebDriverWait wait = new WebDriverWait(driver,10,1);
        Set<Cookie> cookies = null;
        WebElement element = wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
//                cookies = driver.manage().getCookies();
                return driver.findElement(By.xpath("/html/body/div[3]/div[1]/ul/li[3]/table"));
            }
        });
//        driver.manage().addCookie();
//        element.
        //driver.findElement(By.name("request:sn")).sendKeys(new String[]{"27832248"});
        // 点击按扭
        //driver.findElement(By.id("_searchButton")).click();
        try {
            /**
             * WebDriver自带了一个智能等待的方法。
             dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             Arg0：等待的时间长度，int 类型 ；
             Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
           // driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * dr.quit()和dr.close()都可以退出浏览器,简单的说一下两者的区别：第一个close，
         * 如果打开了多个页面是关不干净的，它只关闭当前的一个页面。第二个quit，
         * 是退出了所有Webdriver所有的窗口，退的非常干净，所以推荐使用quit最为一个case退出的方法。
         */
        //driver.quit();//退出浏览器
    }
}
