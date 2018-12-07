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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static strictfp void main(String[] args) {
        //System.out.println(Character.isJavaIdentifierPart('a'));
        String str = "www.runoob.com/w3cnote/programm-sdf-as223er.html";
//        String regex = "http://www\\.runoob\\.com/w3cnote/.+\\.html";
        String regex = "http://www\\.runoob\\.com/w3cnote/page/\\d+\\.html";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.matches());
    }
}
