package com.azureip.tmspider.controller;

import com.azureip.tmspider.pojo.GlobalResponse;
import com.azureip.tmspider.pojo.TmkooQueryPojo;
import com.azureip.tmspider.service.TmkooService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.Properties;

/**
 * 标库网数据获取
 */
@Controller
@RequestMapping("tmkoo")
public class TmkooController {

    private final TmkooService tmkooService;

    @Autowired
    public TmkooController(TmkooService tmkooService) {
        this.tmkooService = tmkooService;
    }

    @GetMapping("test")
    public void test() throws IOException {
        File file = new File("D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\webapp\\static\\ajaxpage.html");
        // File file = new File("D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\webapp\\static\\index.html");
        // InputStream in = new FileInputStream("D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\webapp\\static\\ajaxpage.html");
        Document html = Jsoup.parse(file, "UTF-8");
        Elements elements = html.body().select("table>tbody>tr");
        elements.forEach(element -> {
            System.out.println(element.text());
        });
    }

    @PostMapping("queryRegDataByMonth")
    @ResponseBody
    public GlobalResponse<String> queryRegDataByMonth(TmkooQueryPojo pojo) {
        try {
            tmkooService.queryRegDataByMonth(pojo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
