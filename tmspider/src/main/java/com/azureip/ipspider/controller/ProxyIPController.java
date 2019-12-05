package com.azureip.ipspider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("proxyIP")
public class ProxyIPController {

    @GetMapping("/fetch/xici")
    public void fetchXiCi() throws IOException {
    }
}
