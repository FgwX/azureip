package com.azureip.ipspider.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ProxyIPService {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPService.class);

    public void fetchXiCiProxyIP() throws IOException, ParseException {
        // 时间显示格式为：19-11-11 11:11
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date deadline = calendar.getTime();

        List<String> urlList = new ArrayList<>();
        // urlList.add("https://www.xicidaili.com/nn/");// 匿名
        // urlList.add("https://www.xicidaili.com/nt/");// 普通
        urlList.add("https://www.xicidaili.com/wn/");// HTTPS
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
                    /*XiCiProxyIPPojo pojo = new XiCiProxyIPPojo(
                            proxy.child(1).html(),
                            Integer.parseInt(proxy.child(2).html()),
                            proxy.child(3).text(),
                            proxy.child(4).html(),
                            proxy.child(5).html(),
                            Double.parseDouble(proxy.child(6).child(0).attr("title").replace("秒", "")),
                            Double.parseDouble(proxy.child(7).child(0).attr("title").replace("秒", "")),
                            getXiciSurviveMinutes(proxy.child(8).html()),
                            verifyTime
                    );
                    if (pojo.getPort() != 9999 && pojo.getPort() != 8080 && pojo.getPort() != 80) {
                        System.out.println(pojo.getIp() + ":" + pojo.getPort());
                    }
                    System.out.println(pojo.getIp() + ":" + pojo.getPort());*/
                }
                wait(2000);
            }
        }
    }

    private static Long getXiciSurviveMinutes(String text) {
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
        for (int i = 1; i < 3; i++) {
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
        urlList.add("https://www.freeip.top?protocol=https&country=中国&page=");
        urlList.add("https://www.freeip.top?protocol=http&country=中国&page=");

        for (String url : urlList) {
            int pageNo = 1;
            while (true) {
                Document page = Jsoup.connect(url + pageNo).get();
                Elements proxyList = page.getElementsByTag("table").get(0).child(1).children();
                if (proxyList.size() < 1) {
                    System.err.println("Page " + pageNo + " is empty!");
                    break;
                }
                pageNo++;
                proxyList.forEach(proxy -> {
                    System.out.println(proxy.child(0).text() + ":" + proxy.child(1).text());
                });
                wait(2000);
            }
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
