package com.azureip.tmspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static void main(String[] args) {

//        <input type="hidden" name="request:queryType" value="" id="request_queryType">
//        <input type="hidden" name="request:queryAuto" value="" id="request_queryAuto">
//        <input type="hidden" name="request:queryMode" value="" id="request_queryMode">
//        <input type="hidden" name="request:queryCom" value="2" id="request_queryCom">
//        <input type="hidden" name="request:mn" value="" id="request_mn">
//        <input type="hidden" name="request:ncs" value="" id="request_ncs">
//        <input type="hidden" name="request:nc" value="" id="request_nc">
//        <input type="hidden" name="request:hnc" value="" id="request_hnc">
//        <input type="hidden" name="request:hne" value="" id="request_hne">
//        <input type="hidden" name="request:sn" value="26058913" id="request_sn">
//        <input type="hidden" name="request:imf" value="" id="request_imf">

        String[] inputHid = {"request:nc", "request:ncs", "request:mn", "request:sn", "request:hnc", "request:hne", "request:imf"};
        String[] inputHidVal = {"request:nc", "request:ncs", "request:mn", "request:sn", "request:hnc", "request:hne", "request:imf"};
        String[] inputDis = {"request:queryType", "request:queryAuto", "request:queryMode", "request:tlong", "request:mi"};
        String[] inputDisVal = {"request:queryType", "request:queryAuto", "request:queryMode", "request:tlong", "request:mi"};

        String txt = "";

        List<String> oriList = new ArrayList<>();
        for (int i = 0; i < inputHid.length; i++) {
            String val = inputHidVal[i];
            if (!StringUtils.isEmpty(val)) {
                txt += inputHid[i] + "=" + URLEncoder.encode(val) + "&";
                oriList.add(URLEncoder.encode(val.replace(" ", "")));
            } else {
                oriList.add("");
            }
        }
        for (int i = 0; i < inputDis.length; i++) {
            String val = inputDisVal[i];
            if (!StringUtils.isEmpty(val)) {
                txt += inputDis[i] + "=" + URLEncoder.encode(val) + "&";
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < oriList.size(); i++) {
            if (i==oriList.size()-1){
                sb.append(oriList.get(i));
                continue;
            }
            sb.append(oriList.get(i)+",");
        }
        System.out.println(sb.toString());
        System.out.println("request:md5=" + getMd5(sb.toString()));

    }

    public static String getMd5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
