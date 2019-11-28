package com.azureip.tmspider.enums;

public enum ProxyIPType {
    GN("高匿"), TM("透明");


    ProxyIPType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
