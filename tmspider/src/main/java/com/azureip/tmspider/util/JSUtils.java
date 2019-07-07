package com.azureip.tmspider.util;

import com.eclipsesource.v8.V8;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JSUtils {
    private static final Logger LOG = LogManager.getLogger(JSUtils.class);

    /**
     * 执行POST请求
     */
    public static CloseableHttpResponse crackAnnPost(CloseableHttpClient client, V8 runtime, HttpPost post) throws IOException {
        long startMilli = System.currentTimeMillis();
        // 第一次请求
        CloseableHttpResponse firstResp = client.execute(post);

        // 如果第一次响应成功，直接返回结果
        int statusCode = firstResp.getStatusLine().getStatusCode();
        LOG.info("第一次请求响应状态码：" + statusCode);
        if (statusCode == 200) {
            return firstResp;
        }

        // 获取tmas_cookie、__jsluid和JSESSIONID
        String respCookies = getResponsedCookies(firstResp);
        LOG.info("获取到的[respCookies]：" + respCookies);

        // 获取“__jsl_clearance”
        String jslClearance = getJslClearance(firstResp, runtime);

        post.setHeader("Upgrade-Insecure-Requests", "1");
        if (!StringUtils.isEmpty(respCookies)) {
            post.setHeader("cookie", respCookies + jslClearance);
        } else {
            post.setHeader("cookie", jslClearance);
        }
        // 再次请求，获取数据
        /*try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        CloseableHttpResponse finalResp = client.execute(post);
        LOG.info("第二次请求响应状态码：" + finalResp.getStatusLine().getStatusCode());

        return finalResp;
    }

    // 通过响应头的set-cookie获取名称为tmas_cookie、__jsluid和JSESSIONID的Cookie
    private static String getResponsedCookies(HttpResponse response) {
        StringBuilder cookies = new StringBuilder();
        Header[] headers = response.getHeaders("set-cookie");
        for (Header header : headers) {
            String value = header.getValue();
            String[] cookieArr = value.split(";");
            for (String cookie : cookieArr) {
                if (cookie.contains("tmas_cookie") || cookie.contains("__jsluid") || cookie.contains("JSESSIONID")) {
                    cookies.append(cookie.trim()).append(";");
                }
            }
        }
        return cookies.toString();
    }

    // 通过响应体获取名称为“__jsl_clearance”的Cookie
    private static String getJslClearance(HttpResponse response, V8 runtime) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        // LOG.info("原始JS为：" + entity.substring("<script>".length(), entity.indexOf("</script>")));
        String[] orgArr = entity.substring("<script>".length(), entity.indexOf("</script>")).split("eval");
        StringBuilder orgJS = new StringBuilder(orgArr[0]);
        for (int i = 1; i < orgArr.length; i++) {
            if (i == orgArr.length - 1) {
                orgJS.append(orgArr[i]);
            } else {
                orgJS.append("eval").append(orgArr[i]);
            }
        }
        String realJS = runtime.executeStringScript(orgJS.toString());
        // LOG.info("实际JS为：" + realJS);
        // 获取jslClearance的前半段
        int aStart = realJS.indexOf("__jsl_clearance");
        int aEnd = realJS.indexOf("|0|") + "|0|".length();
        String jslClrA = realJS.substring(aStart, aEnd);
        LOG.info("获取到的[__jsl_clearance]前半段：" + jslClrA);

        // 获取jslClearance的后半段
        // int tmpStart = realJS.indexOf("function(){", aEnd) + "function(){".length();
        int tmpStart = realJS.indexOf("(function(){");
        // int tmpEnd = realJS.indexOf("})()+';Expires");
        int tmpEnd = realJS.indexOf("+';Expires");
        // String tmpFinalJS = realJS.substring(tmpStart, tmpEnd);
        // LOG.info("获取[__jsl_clearance]后半段临时JS：" + tmpFinalJS);
        // String[] finalArr = tmpFinalJS.split("return");
        // StringBuilder finalJSSB = new StringBuilder(finalArr[0]);
        // for (int i = 1; i < finalArr.length; i++) {
        //     if (i == finalArr.length - 1) {
        //         finalJSSB.append(finalArr[i]);
        //     } else {
        //         finalJSSB.append("return").append(finalArr[i]);
        //     }
        // }
        // String finalJS = finalJSSB.toString();
        String finalJS = realJS.substring(tmpStart, tmpEnd);
        if (finalJS.indexOf("document") > 0 && finalJS.indexOf("toLowerCase()") > 0) {
            int start = finalJS.indexOf("document");
            int end = finalJS.indexOf("toLowerCase()") + "toLowerCase()".length();
            String nonJS = finalJS.substring(start, end);
            finalJS = finalJS.replace(nonJS, "'sbgg.saic.gov.cn:9080/'");
        }
        finalJS = finalJS.replace("window.headless", "0")
                .replace("window['__p'+'hantom'+'as']", "undefined")
                .replace("window['callP'+'hantom']", "undefined")
                .replace("window['_p'+'hantom']", "undefined");
        // LOG.info("获取[__jsl_clearance]后半段JS：" + finalJS);
        String jslClrB = runtime.executeStringScript(finalJS);
        LOG.info("获取到的[__jsl_clearance]后半段：" + jslClrB);
        runtime.release();
        return jslClrA + jslClrB;
    }

    /**
     * TODO  方法补全
     * 将HTTP响应体转换为字符串返回
     */
    private static String getResponseBodyAsString(HttpResponse response) throws IOException {
        // return IOUtils.readStreamAsString(response.getEntity().getContent(), "UTF-8");
        return null;
    }

    /**
     * TODO  方法补全
     * 将HTTP响应体转换为byte数组返回
     */
    private static byte[] getResponseBodyAsBytes(HttpResponse response) throws IOException {
        // return IOUtils.readStreamAsByteArray(response.getEntity().getContent());
        return null;
    }

}
