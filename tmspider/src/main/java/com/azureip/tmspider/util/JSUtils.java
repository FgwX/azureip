package com.azureip.tmspider.util;

import com.eclipsesource.v8.V8;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JSUtils {

    /**
     * 执行POST请求
     */
    public static HttpResponse crackAnnPost(CloseableHttpClient client, HttpPost post) throws IOException {
        // 第一次请求
        HttpResponse firstResp = client.execute(post);
        System.out.println("第一次请求响应状态码：" + firstResp.getStatusLine().getStatusCode());

        // 获取“__jsluid”
        String jslUid = getJslUid(firstResp);
        // 获取“__jsl_clearance”
        // globalAlias=window，表示window为全局别名，告诉V8在运行JavaScript代码时，不要从代码里找window的定义
        V8 runtime = V8.createV8Runtime();
        String jslClearance = getJslClearance(firstResp, runtime);

        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("cookie", jslUid + "; " + jslClearance);
        // 再次请求，获取数据
        CloseableHttpResponse finalResp = client.execute(post);
        System.out.println("第二次请求响应状态码：" + finalResp.getStatusLine().getStatusCode());

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
        String evalFrag = "eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}))";
        String execFrag = "y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)})";
        String orgJS = entity.substring("<script>".length(), entity.indexOf("</script>")).replace(evalFrag, execFrag);
        System.err.println("原始JS：" + orgJS);
        String realJS = runtime.executeStringScript(orgJS);
        System.err.println("实际JS：" + realJS);
        // 获取jslClearance的前半段
        int aStart = realJS.indexOf("__jsl_clearance");
        int aEnd = realJS.indexOf("|0|") + "|0|".length();
        String jslClrA = realJS.substring(aStart, aEnd);

        // 获取jslClearance的后半段
        int bStart = realJS.indexOf("|0|'+(function(){") + "|0|'+(function(){".length();
        int bEnd = realJS.indexOf("})()+';Expires");
        String tmpStr = realJS.substring(bStart, bEnd);
        String unUsedFn = tmpStr.substring(tmpStr.indexOf(",(function(){"), (tmpStr.indexOf("join('')}})()") + "join('')}})()".length()));
        String unUsedStr = tmpStr.substring(tmpStr.indexOf("]](") + 2, tmpStr.indexOf("};return", tmpStr.indexOf("reverse")));
        System.err.println("unUsedFn: " + unUsedFn + "\r\nunUsedStr: " + unUsedStr);
        String finalJS = tmpStr.replace(unUsedFn, "").replace(unUsedStr, "")
                .replace("window.headless", "0")
                .replace(";return", ";")
                .replace("; function", ";function tmp");
        System.err.println("最终JS：" + finalJS);
        String jslClrB = runtime.executeStringScript(finalJS);

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
