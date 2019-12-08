package com.azureip.ipspider.controller;

import com.azureip.ipspider.service.ProxyIPFetchService;
import com.azureip.ipspider.service.ProxyIPVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("proxy")
public class ProxyIPController {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyIPController.class);
    @Autowired
    private ProxyIPFetchService proxyIPFetchService;
    @Autowired
    private ProxyIPVerifyService proxyIPVerifyService;

    @GetMapping("/fetch/xici")
    public void fetchXiCiProxyIP() throws IOException, ParseException {
        proxyIPFetchService.fetchXiCiProxyIP();
    }

    @GetMapping("/fetch/66")
    public void fetch66ProxyIP() throws IOException {
        proxyIPFetchService.fetch66ProxyIP();
    }

    @GetMapping("/fetch/89")
    public void fetch89ProxyIP() throws IOException {
        proxyIPFetchService.fetch89ProxyIP();
    }

    @GetMapping("/fetch/free")
    public void fetchFreeIPProxyIP() throws IOException {
        proxyIPFetchService.fetchFreeIPProxyIP();
    }
}
