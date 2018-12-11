package com.azureip.tmspider.service;

import com.azureip.tmspider.mapper.TMKooRecordMapper;
import com.azureip.tmspider.model.TMKooRecord;
import com.azureip.tmspider.pojo.TmkooQueryPojo;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TmkooService {

    private final TMKooRecordMapper tmKooRecordMapper;

    private static final String DOMAIN_URL = "http://www.tmkoo.com";
    private static final String LOGIN_COOKIE_URL = "http://www.tmkoo.com/j_spring_security_check";
    private static final String QUERY_SESSION_KEY_URL = "http://www.tmkoo.com/searchmore/adv!ajaxSearch.php";
    private static final String QUERY_PAGE_URL = "http://www.tmkoo.com/searchmore/adv!ajaxPage.php";

    private static CookieStore cookieStore = new BasicCookieStore();

    @Autowired
    public TmkooService(TMKooRecordMapper tmKooRecordMapper) {
        this.tmKooRecordMapper = tmKooRecordMapper;
    }

    public String importRegDataByMonth(TmkooQueryPojo pojo) throws IOException, ParseException {
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
        headers.add(new BasicHeader("Host", "www.tmkoo.com"));
        headers.add(new BasicHeader("Origin", "http://www.tmkoo.com"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36"));
        long start = System.currentTimeMillis();
        // 一、模拟登陆，获取并设置Cookie
        List<NameValuePair> loginParams = new ArrayList<>();
        loginParams.add(new BasicNameValuePair("j_username", "15930771833"));
        loginParams.add(new BasicNameValuePair("j_password", "lihuan328829"));
        System.out.println("[Step 01].Begin...");

        CloseableHttpResponse loginResponse = executePostRequest(client, LOGIN_COOKIE_URL, headers, loginParams);
        loginResponse.close();
        long loginEnd = System.currentTimeMillis();
        System.out.println("[Step 04].Login success in " + (loginEnd - start) + "ms");

        // 二、拼装条件，查询Session的Key值（本次查询的身份标识）
        CloseableHttpResponse keyResponse = executePostRequest(client, QUERY_SESSION_KEY_URL, headers, convertToPairParams(pojo));
        // SUCCESS:201812081017111565221:1;2;3;4
        String keyResponseStr = EntityUtils.toString(keyResponse.getEntity());
        keyResponse.close();
        if (!"SUCCESS".equals(keyResponseStr.split(":")[0]))
            return "Failed";
        System.out.println("[Step 05].SessionKey: " + keyResponseStr.split(":")[1]);
        pojo.setL(keyResponseStr.split(":")[1]);
        long queryKeyEnd = System.currentTimeMillis();
        System.out.println("[Step 06].SessionKey query cost " + (queryKeyEnd - loginEnd) + "ms");

        // 三、进行分页查询，获取数据并写入表格
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int maxPageNo = 1;
        AtomicInteger pageNo = new AtomicInteger(1);
        while (pageNo.get() <= maxPageNo) {
            // 请求获取列表页面
            pojo.setPageNo(pageNo.toString());
            CloseableHttpResponse pageResponse = executePostRequest(client, QUERY_PAGE_URL, headers, convertToPairParams(pojo));
            Document listPage = Jsoup.parse(EntityUtils.toString(pageResponse.getEntity(), "UTF-8"));
            pageResponse.close();
            // 首次请求获取总页数
            if (pageNo.get() == 1) {
                String navText = listPage.select("body>div:eq(0)").text();
                String totalPage = navText.split(",")[0];
                maxPageNo = Integer.parseInt(totalPage.substring(2, totalPage.length() - 1));
            }

            // 多线程调用
            getDetailByPage(client, pageNo.get(), listPage, dateFormat);
            pageNo.incrementAndGet();
        }
        long listQueryEnd = System.currentTimeMillis();
        System.out.println("【详情查询导入】耗时：" + (listQueryEnd - queryKeyEnd) + "毫秒…");
        System.out.println("共计耗时：" + (int) (listQueryEnd - start) / 1000 + "秒。");

        client.close();
        return "Success";
    }

    @Async("tmAsyncExecutor")
    public void getDetailByPage(CloseableHttpClient client, int pageNo, Document listPage, SimpleDateFormat dateFormat)
            throws IOException, ParseException {
        // 获取所有列表项
        Elements regList = listPage.select("body>table>tbody>tr");
        for (Element regData : regList) {
            // 请求详情页
            String regDetailUrl = DOMAIN_URL + regData.select("td:eq(1)>a").attr("href");
            CloseableHttpResponse detailResponse = client.execute(new HttpGet(regDetailUrl));
            Document detailHtml = Jsoup.parse(EntityUtils.toString(detailResponse.getEntity(), "UTF-8"));
            Element detailBody = detailHtml.body();
            TMKooRecord record = new TMKooRecord();
            record.setRegNum(regData.select("td:eq(1)").text());
            record.setTmName(regData.select("td:eq(3)").text());
            record.setTmType(Integer.parseInt(regData.select("td:eq(2)").text()));
            record.setAppName(detailBody.select("#wd").val());
            Date appDate = dateFormat.parse(regData.select("td:eq(4)").text());
            record.setAppDate(appDate);
            record.setAppAddress(detailBody.select("div.result>table>tbody>tr:eq(2)>td:eq(1)").text());
            System.out.println("Page[" + pageNo + "]: " + record.getRegNum() + "|" + record.getTmName() + "|" + record.getTmType() + "|" + record.getAppName() + "|"
                    + record.getAppDate().toLocaleString() + "|" + record.getAppAddress());

            try {
                tmKooRecordMapper.insert(record);
            } catch (Exception e) {
                System.out.println("Insert cause exception: " + e.getMessage());
            }
        }
    }

    // 执行GET请求
    private CloseableHttpResponse executeGetRequest(CloseableHttpClient client, String url, List<Header> headers) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeaders(headers.toArray(new Header[0]));
        return client.execute(get);
    }

    // 执行POST请求
    private CloseableHttpResponse executePostRequest(CloseableHttpClient client, String url, List<Header> headers, List<NameValuePair> params) throws IOException {
        HttpPost loginPost = new HttpPost(url);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        loginPost.setHeaders(headers.toArray(new Header[0]));
        loginPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(loginPost);
        if (null != response.getFirstHeader("Set-Cookie")) {
            System.out.println("[Step 02].Setting cookie...");
            // Cookie: JSESSIONID=A6D67124762076255F3E20D1B4FAD740; Path=/; HttpOnly
            String setCookie = response.getFirstHeader("Set-Cookie").getValue();
            response.close();
            String jSessionID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
            BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", jSessionID);
            cookie.setPath("/");
            cookie.setDomain("www.tmkoo.com");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 20);
            cookie.setExpiryDate(calendar.getTime());
            cookieStore.addCookie(cookie);
            System.out.println("[Step 03].Printing cookie: " + cookie);
        }
        return response;
    }

    private void setCookieStore(HttpResponse response, String host) {
        // Cookie: JSESSIONID=A6D67124762076255F3E20D1B4FAD740; Path=/; HttpOnly
        String setCookie = response.getFirstHeader("Set-Cookie").getValue();
        String jSessionID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", jSessionID);
        cookie.setPath("/");
        cookie.setDomain(host);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 20);
        cookie.setExpiryDate(calendar.getTime());
        cookieStore.addCookie(cookie);
    }

    // 请求参数转化
    private List<NameValuePair> convertToPairParams(TmkooQueryPojo pojo) {
        List<NameValuePair> params = new ArrayList<>();
        if (!StringUtils.isEmpty(pojo.getOrderBy()))
            params.add(new BasicNameValuePair("orderBy", pojo.getOrderBy()));
        if (!StringUtils.isEmpty(pojo.getOrder()))
            params.add(new BasicNameValuePair("order", pojo.getOrder()));
        if (!StringUtils.isEmpty(pojo.getD()))
            params.add(new BasicNameValuePair("d", pojo.getD()));
        if (!StringUtils.isEmpty(pojo.getL()))
            params.add(new BasicNameValuePair("l", pojo.getL()));
        if (!StringUtils.isEmpty(pojo.getLczt()))
            params.add(new BasicNameValuePair("lczt", pojo.getLczt()));
        if (!StringUtils.isEmpty(pojo.getGjfl()))
            params.add(new BasicNameValuePair("gjfl", pojo.getGjfl()));
        if (!StringUtils.isEmpty(pojo.getQzhs()))
            params.add(new BasicNameValuePair("qzhs", pojo.getQzhs()));
        if (!StringUtils.isEmpty(pojo.getGoods()))
            params.add(new BasicNameValuePair("goods", pojo.getGoods()));
        if (!StringUtils.isEmpty(pojo.getCsggqhStart()))
            params.add(new BasicNameValuePair("csggqhStart", pojo.getCsggqhStart()));
        if (!StringUtils.isEmpty(pojo.getCsggqhEnd()))
            params.add(new BasicNameValuePair("csggqhEnd", pojo.getCsggqhEnd()));
        if (!StringUtils.isEmpty(pojo.getZcggqhStart()))
            params.add(new BasicNameValuePair("zcggqhStart", pojo.getZcggqhStart()));
        if (!StringUtils.isEmpty(pojo.getZcggqhEnd()))
            params.add(new BasicNameValuePair("zcggqhEnd", pojo.getZcggqhEnd()));
        if (!StringUtils.isEmpty(pojo.getCsggrqStart()))
            params.add(new BasicNameValuePair("csggrqStart", pojo.getCsggrqStart()));
        if (!StringUtils.isEmpty(pojo.getCsggrqEnd()))
            params.add(new BasicNameValuePair("csggrqEnd", pojo.getCsggrqEnd()));
        if (!StringUtils.isEmpty(pojo.getZcggrqStart()))
            params.add(new BasicNameValuePair("zcggrqStart", pojo.getZcggrqStart()));
        if (!StringUtils.isEmpty(pojo.getZcggrqEnd()))
            params.add(new BasicNameValuePair("zcggrqEnd", pojo.getZcggrqEnd()));
        if (!StringUtils.isEmpty(pojo.getSqrqStart()))
            params.add(new BasicNameValuePair("sqrqStart", pojo.getSqrqStart()));
        if (!StringUtils.isEmpty(pojo.getSqrqEnd()))
            params.add(new BasicNameValuePair("sqrqEnd", pojo.getSqrqEnd()));
        if (!StringUtils.isEmpty(pojo.getSqrmcZw()))
            params.add(new BasicNameValuePair("sqrmcZw", pojo.getSqrmcZw()));
        if (!StringUtils.isEmpty(pojo.getDlrmc()))
            params.add(new BasicNameValuePair("dlrmc", pojo.getDlrmc()));
        if (!StringUtils.isEmpty(pojo.getSqrdzZw()))
            params.add(new BasicNameValuePair("sqrdzZw", pojo.getSqrdzZw()));
        if (!StringUtils.isEmpty(pojo.getPageNo()))
            params.add(new BasicNameValuePair("pageNo", pojo.getPageNo()));
        return params;
    }
}
