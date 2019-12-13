package com.azureip.ipspider.service;

import com.azureip.ipspider.mapper.ProxyIPMapper;
import com.azureip.ipspider.model.ProxyIP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 代理IP抓取相关服务类
 * @author LewisZhang
 * 5th.Dec.2019
 */
@Service
public class ProxyIPFetchService {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPFetchService.class);
    @Autowired(required = false)
    private ProxyIPMapper proxyIPMapper;

    public void fetchXiCiProxyIP() throws IOException, ParseException {
        // 时间显示格式为：19-11-11 11:11
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date deadline = calendar.getTime();

        List<String> urlList = new ArrayList<>();
        // urlList.add("https://www.xicidaili.com/nn/");// 匿名
        // urlList.add("https://www.xicidaili.com/nt/");// 普通
        // urlList.add("https://www.xicidaili.com/wn/");// HTTPS
        urlList.add("https://www.xicidaili.com/wt/");// HTTP

        for (String url : urlList) {
            System.out.println("URL: " + url);
            boolean inOneDay = true;
            int pageNo = 1;
            while (inOneDay) {
                System.out.println("PageNo: " + pageNo);
                Document page = Jsoup.connect(url + pageNo).get();
                pageNo++;
                Elements proxyList = page.getElementById("ip_list").getElementsByTag("tbody").get(0).children();
                for (int i = 1; i < proxyList.size(); i++) {
                    Element proxy = proxyList.get(i);
                    Date verifyTime = format.parse(proxy.child(9).text());
                    if (verifyTime.before(deadline)) {
                        inOneDay = false;
                        break;
                    }
                    System.out.println(proxy.child(1).text() + ":" + proxy.child(2).text());
                    ProxyIP existProxy = proxyIPMapper.selectByPrimaryKey(proxy.child(1).text(), Integer.parseInt(proxy.child(2).text()));

                }
                wait(3000);
            }
        }
    }

    private static Long getXiCiSurviveMinutes(String text) {
        if (text.indexOf("分钟") > 0) {
            return Long.parseLong(text.replace("分钟", ""));
        } else if (text.indexOf("小时") > 0) {
            return Long.parseLong(text.replace("小时", "")) * 60;
        } else if (text.indexOf("天") > 0) {
            return Long.parseLong(text.replace("天", "")) * 24 * 60;
        } else {
            return 0L;
        }
    }

    public void fetch66ProxyIP() throws IOException {
        // 获取主页索引列表
        String indexURL = "http://www.66ip.cn";
        Document indexPage = Jsoup.connect(indexURL).get();
        Elements indexList = indexPage.getElementById("main").previousElementSibling().getElementsByTag("ul").get(0).children();

        // 根据索引列表获取IP
        for (int i = 1; i < indexList.size(); i++) {
            Element a = indexList.get(i).child(0);
            String url = indexURL + a.attr("href");
            System.out.println(a.text() + "：" + url);
            Document listPage = Jsoup.connect(url).get();
            Element table = listPage.getElementById("main").getElementsByTag("table").get(0);
            Elements ipList = table.child(0).children();
            for (int j = 1; j < ipList.size(); j++) {
                Element e = ipList.get(j);
                System.out.println(e.child(0).text() + ":" + e.child(1).text());
            }
            wait(800);
        }
    }

    public void fetch89ProxyIP() throws IOException {
        int pageNo = 1;
        while (true) {
            String url = "http://www.89ip.cn/index_" + pageNo + ".html";
            System.out.println("Page " + pageNo + ": " + url);
            Document page = Jsoup.connect(url).get();
            Elements proxyList = page.getElementsByTag("table").get(0).child(1).children();
            if (proxyList.size() < 1) {
                System.err.println("Page " + pageNo + " is empty!");
                break;
            }
            pageNo += 1;
            proxyList.forEach(proxy -> {
                System.out.println(proxy.child(0).text() + ":" + proxy.child(1).text());
            });
            wait(800);
        }
    }

    public void fetchFreeIPProxyIP() throws IOException {
        List<String> urlList = new ArrayList<>();
        // urlList.add("https://www.freeip.top?protocol=https&country=中国&page=");
        urlList.add("https://www.freeip.top?protocol=http&country=中国&page=");

        for (String url : urlList) {
            int pageNo = 1;
            while (true) {
                Document page = Jsoup.connect(url + pageNo).get();
                Elements proxyEleList = page.getElementsByTag("table").get(0).child(1).children();
                if (proxyEleList.size() < 1) {
                    System.err.println("Page " + pageNo + " is empty!");
                    break;
                }
                pageNo++;

                List<ProxyIP> proxyList = new ArrayList<>();
                proxyEleList.forEach(proxyEle -> {
                    String ip = proxyEle.child(0).text();
                    int port = Integer.parseInt(proxyEle.child(1).text());
                    System.out.println(ip + ":" + port);
                    // 插入数据库
                    /*ProxyIP existProxy = proxyIPMapper.selectByPrimaryKey(ip, port);
                    if (existProxy == null) {
                        String typeStr = proxyEle.child(3).text();
                        System.out.println("新  增: " + ip + ":" + port + ", " + typeStr);
                        Integer type = matchProxyType(typeStr);
                        ProxyIP proxy = new ProxyIP(ip, port, type);
                        proxyList.add(proxy);
                    } else {
                        System.out.println("已存在: " + ip + ":" + port);
                    }*/
                });
                // proxyIPMapper.saveAll(proxyList);
                wait(2000);
            }
        }
    }

    private Integer matchProxyType(String type) {
        switch (type.toUpperCase()) {
            case "HTTP":
                return 1;
            case "HTTPS":
                return 2;
            case "SOCKET":
                return 3;
            case "SOCKET5":
                return 3;
            default:
                return 0;
        }
    }

    private void wait(int milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
