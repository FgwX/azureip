package com.azureip.ipspider.service;

import com.azureip.common.tool.CircularLinkedQueue;
import com.azureip.ipspider.model.ProxyIP;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

@Service
public class ProxyIPProvider {
    private static ArrayBlockingQueue<ProxyIP> cacheIPQueue = new ArrayBlockingQueue<>(5);
    private static CircularLinkedQueue<ProxyIP> queue;

    static {
        queue = new CircularLinkedQueue<>();
        queue.add(new ProxyIP("115.159.31.195:8080"));
        queue.add(new ProxyIP("183.146.213.198:80"));
        queue.add(new ProxyIP("101.231.104.82:80"));
        queue.add(new ProxyIP("101.4.136.34:81"));
        queue.add(new ProxyIP("101.95.115.196:8080"));
        queue.add(new ProxyIP("116.114.19.204:443"));
        queue.add(new ProxyIP("116.114.19.211:443"));
        queue.add(new ProxyIP("118.89.234.236:8787"));
        queue.add(new ProxyIP("119.41.236.180:8010"));
        queue.add(new ProxyIP("183.220.145.3:80"));
        queue.add(new ProxyIP("218.60.8.99:3129"));
        queue.add(new ProxyIP("222.175.171.6:8080"));
        queue.add(new ProxyIP("223.111.131.100:80"));
        queue.add(new ProxyIP("39.137.107.98:80"));
        queue.add(new ProxyIP("39.137.69.10:80"));
        queue.add(new ProxyIP("39.137.69.6:80"));
        queue.add(new ProxyIP("39.137.69.7:80"));
        queue.add(new ProxyIP("39.137.69.8:80"));
        queue.add(new ProxyIP("39.137.69.9:80"));
        queue.add(new ProxyIP("52.80.58.248:3128"));
    }

    public ProxyIP nextProxy() {
        return queue.next();
    }
}