package com.azureip.tmspider.constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TMSpider全局常量
 */
public class TMSConstant {

    // 驳回日期格式化
    public static final SimpleDateFormat REJ_DATE_FMT = new SimpleDateFormat("yyyy年MM月dd日");
    public static final String REJ_DATE_REGEX = "yyyy年MM月dd日";
    public static final Date DEF_DATE = new Date(0);

    // 商标状态查询域名
    public static final String STATUS_DOMAIN = "http://wcjs.sbj.cnipa.gov.cn";

    // 商标公告查询域名
    public static final String ANN_DOMAIN = "http://wsgg.sbj.cnipa.gov.cn:9080";
}
