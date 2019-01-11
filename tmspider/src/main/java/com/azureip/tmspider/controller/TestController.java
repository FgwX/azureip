package com.azureip.tmspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.Random;

@Controller
@RequestMapping("test")
public class TestController {

    @GetMapping("test")
    public String test() {
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static void main(String[] args) throws ParseException {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.DAY_OF_MONTH, 1);
        // Date firstDayOfCurrentMonth = sdf.parse(sdf.format(calendar.getTime()));
        // System.out.println(sdf2.format(firstDayOfCurrentMonth));
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(10));
        }
    }
}
