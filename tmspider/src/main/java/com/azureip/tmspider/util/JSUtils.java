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

import java.io.IOException;

public class JSUtils {
    private static final Logger LOG = LogManager.getLogger(JSUtils.class);

    /**
     * 执行POST请求
     */
    public static CloseableHttpResponse crackAnnPost(CloseableHttpClient client,V8 runtime,  HttpPost post) throws IOException {
        long startMilli = System.currentTimeMillis();
        // 第一次请求
        CloseableHttpResponse firstResp = client.execute(post);
        LOG.info("第一次请求响应状态码：" + firstResp.getStatusLine().getStatusCode());

        // 获取“__jsluid”
        String jslUid = getJslUid(firstResp);
        LOG.info("获取到的[__jsluid]：" + jslUid);
        // 获取“__jsl_clearance”

        String jslClearance = getJslClearance(firstResp, runtime);

        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("cookie", jslUid + "; " + jslClearance);
        // 再次请求，获取数据
        while (true) {
            if (System.currentTimeMillis() - startMilli >= 1500) {
                break;
            }
        }
        CloseableHttpResponse finalResp = client.execute(post);
        LOG.info("第二次请求响应状态码：" + finalResp.getStatusLine().getStatusCode());

        return finalResp;
    }

    // 通过响应头的set-cookie获取名称为“__jsluid”的Cookie
    private static String getJslUid(HttpResponse response) {
        Header header = response.getFirstHeader("set-cookie");
        if (header == null) {
            return "";
        }
        String[] cookies = header.getValue().split(";");
        for (String cookie : cookies) {
            if (cookie.contains("__jsluid")) {
                return cookie.trim();
            }
        }
        return "";
    }

    // 通过响应体获取名称为“__jsl_clearance”的Cookie
    private static String getJslClearance(HttpResponse response, V8 runtime) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
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
        // 获取jslClearance的前半段
        int aStart = realJS.indexOf("__jsl_clearance");
        int aEnd = realJS.indexOf("|0|") + "|0|".length();
        String jslClrA = realJS.substring(aStart, aEnd);
        LOG.info("获取到的[__jsl_clearance]前半段：" + jslClrA);
        // 获取jslClearance的后半段

        int tmpStart = realJS.indexOf("function(){", aEnd) + "function(){".length();
        int tmpEnd = realJS.indexOf("})()+';Expires");
        String tmpFinalJS = realJS.substring(tmpStart, tmpEnd);
        String[] finalArr = tmpFinalJS.split("return");
        StringBuilder finalJSSB = new StringBuilder(finalArr[0]);
        for (int i = 1; i < finalArr.length; i++) {
            if (i == finalArr.length - 1) {
                finalJSSB.append(finalArr[i]);
            } else {
                finalJSSB.append("return").append(finalArr[i]);
            }
        }
        String finalJS = finalJSSB.toString();
        int start = finalJS.indexOf("document");
        int end = finalJS.indexOf("toLowerCase()") + "toLowerCase()".length();
        String nonJS = finalJS.substring(start, end);
        finalJS = finalJS.replace(nonJS, "'sbgg.saic.gov.cn:9080/'")
                .replace("window.headless", "0")
                .replace("window['__p'+'hantom'+'as']", "undefined")
                .replace("window['callP'+'hantom']", "undefined")
                .replace("window['_p'+'hantom']", "undefined");
        LOG.info("获取[__jsl_clearance]后半段JS：" + finalJS);
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
