package com.azureip.common.constant;

import java.text.SimpleDateFormat;

public class Constant {

    public static final String USER_AGENT = "";

    // 驳回日期格式化
    public static final SimpleDateFormat COMMON_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String COMMON_DATE_REGEX = "yyyy-MM-dd HH:mm:ss";

    // 初始化浏览器类型
    public static final String WEB_DRIVER_CHROME = "CHROME";
    public static final String WEB_DRIVER_FIREFOX = "FIREFOX";
    public static final String WEB_DRIVER_HTML = "HTML";
}
