package com.azureip.tmspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.util.Calendar;
@Controller
public class TestController {

    @GetMapping("test")
    public String test(){
        System.out.println("Hello Test!");
        return "forward:index.html";
    }

    public static void main(String[] args) {
//        TestPojo pojo = new TestPojo("testName", 18);
//        Gson gson = new Gson();
        // 测试
        Calendar c = Calendar.getInstance();
        //c.setTimeInMillis(1526232653596l);
        c.set(2018, 4, 14, 1, 30, 53);
        System.out.println(c.getTimeInMillis());


    }

    private static class TestPojo implements Serializable {
        private String name;
        private int age;

        public TestPojo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAge() {

            return age;
        }
    }
}
