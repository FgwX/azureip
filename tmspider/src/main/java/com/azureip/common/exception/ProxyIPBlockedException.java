package com.azureip.common.exception;

public class ProxyIPBlockedException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Current ip address is blocked by target host!";
    }
}
