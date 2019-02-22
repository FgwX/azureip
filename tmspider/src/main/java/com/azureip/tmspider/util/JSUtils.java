package com.azureip.tmspider.util;

import com.eclipsesource.v8.V8;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JSUtils {
    private static final Logger LOG = LogManager.getLogger(JSUtils.class);

    /**
     * 传入pdf文件的URL, 返回下载好的PDF文件
     */
    public static File getPDFFile(String pdfUrl) {
        File file = new File("D:/test.pdf");
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(pdfUrl);
            setHeader(get);
            HttpResponse response = client.execute(get);

            String __jsluid = getJsluid(response);
            String body = getResponseBodyAsString(response);
            String __jsl_clearance = getJslClearance(body);
            get = new HttpGet(pdfUrl);
            get.setHeader("cookie", __jsluid + "; " + __jsl_clearance);
            setHeader(get);
            response = client.execute(get);
            output(response, file);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 给HttpGet设置一些必要的header
     */
    private static void setHeader(HttpGet get) {
        get.setHeader("Upgrade-Insecure-Requests", "1");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
    }

    /**
     * 将HttpResponse输出到文件, 即将pdf输入流写到硬盘.
     */
    private static void output(HttpResponse response, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(getResponseBodyAsBytes(response));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 通过破解动态JavaScript脚本, 获取cookie名为 __jsl_clearance的值
     */
    private static String getJslClearance(String body) {
        //V8:谷歌开源的运行JavaScript脚本的库. 参数:globalAlias=window, 表示window为全局别名,
        // 告诉V8在运行JavaScript代码时, 不要从代码里找window的定义.
        V8 runtime = V8.createV8Runtime("window");
        //将第一次请求pdf资源时获取到的字符串提取成V8可执行的JavaScript代码
        body = body.trim()
                .replace("<script>", "")
                .replace("</script>", "")
                .replace("eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}))",
                        "y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)})");
        //用V8执行该段代码获取新的动态JavaScript脚本
        String result = runtime.executeStringScript(body);

        //获取 jsl_clearance 的第一段, 格式形如: 1543915851.312|0|
        String startStr = "document.cookie='";
        int i1 = result.indexOf(startStr) + startStr.length();
        int i2 = result.indexOf("|0|");
        String cookie1 = result.substring(i1, i2 + 3);

        /* 获取 jsl_clearance 的第二段,格式形如: DW2jqgJO5Bo45yYRKLlFbnqQuD0%3D。
        主要原理是: 新的动态JavaScript脚本是为浏览器设置cookie, 且cookie名为__jsl_clearance
        其中第一段值(格式形如:1543915851.312|0|)已经明文写好, 用字符串处理方法即可获取.
        第二段则是一段JavaScript函数, 需要有V8运行返回,
        该函数代码需要通过一些字符串定位, 提取出来, 交给V8运行.*/
        startStr = "|0|'+(function(){";
        int i3 = result.indexOf(startStr) + startStr.length();
        int i4 = result.indexOf("})()+';Expires");
        String code = result.substring(i3, i4).replace(";return", ";");
        String cookie2 = runtime.executeStringScript(code);

        // 拼接两段字符串，返回jsl_clearance的完整的值。如: 1543915851.312|0|DW2jqgJO5Bo45yYRKLlFbnqQuD0%3D
        return cookie1 + cookie2;
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

    /**
     * 通过响应头的set-cookie获取cookie名称为__jsluid的值
     */
    private static String getJsluid(HttpResponse response) {
        Header header = response.getFirstHeader("set-cookie");
        String[] split = header.getValue().split(";");
        for (String s : split) {
            if (s.contains("__jsluid")) {
                return s.trim();
            }
        }
        return "";
    }
}
