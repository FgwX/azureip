package com.azureip.tmspider.controller;

import com.azureip.common.pojo.GlobalResponse;
import com.azureip.tmspider.pojo.TmkooQueryPojo;
import com.azureip.tmspider.service.TmkooService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("tmkoo")
public class TmkooController {

    private final TmkooService tmkooService;

    @Autowired
    public TmkooController(TmkooService tmkooService) {
        this.tmkooService = tmkooService;
    }

    @PostMapping("importTMKooRecords")
    @ResponseBody
    public GlobalResponse<String> importTMKooRecords(TmkooQueryPojo pojo) {
        try {
            return tmkooService.importRegDataByMonth(pojo);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new GlobalResponse<>(GlobalResponse.ERROR, e.getMessage());
        }
    }

    @GetMapping("test")
    @ResponseBody
    public GlobalResponse test() {
        try {
            File detailFile = new File("D:\\Project\\IDEA\\azureip\\tmspider\\src\\main\\webapp\\static\\html\\tmkoo\\detail.html");
            System.out.println(detailFile.toString());
            Document detailDoc = Jsoup.parse(detailFile, "UTF-8");
            Elements appName = detailDoc.body().select("#wd");
            System.out.println(appName.val());
            Elements appAddress = detailDoc.body().select(".result>table>tbody>tr:eq(2)>td:eq(1)");
            System.out.println(appAddress.text());
        } catch (IOException e) {
            e.printStackTrace();
            return new GlobalResponse(GlobalResponse.ERROR, "ERROR");
        }
        return new GlobalResponse(GlobalResponse.SUCCESS, "SUCCESS");
    }

}
