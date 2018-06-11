package com.azureip.tmspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String index() {
        System.out.println("Requesting to index...");
        return "forward:index.html";
    }
}
