package com.azureip.tmspider.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XiCiProxyIPPojo {
    private static final SimpleDateFormat F = new SimpleDateFormat("yy-MM-dd HH:mm");

    private String ip;
    private Integer port;
    private String location;
    private String privacyType;
    private String proxyType;
    private Double lag;
    private Double connectTime;
    private Long surviveTime;
    private Date verifyTime;

    public XiCiProxyIPPojo() {
    }

    public XiCiProxyIPPojo(String ip, Integer port, String location, String privacyType, String proxyType, Double lag, Double connectTime, Long surviveTime, Date verifyTime) {
        this.ip = ip;
        this.port = port;
        this.location = location;
        this.privacyType = privacyType;
        this.proxyType = proxyType;
        this.lag = lag;
        this.connectTime = connectTime;
        this.surviveTime = surviveTime;
        this.verifyTime = verifyTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public Double getLag() {
        return lag;
    }

    public void setLag(Double lag) {
        this.lag = lag;
    }

    public Double getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Double connectTime) {
        this.connectTime = connectTime;
    }

    public Long getSurviveTime() {
        return surviveTime;
    }

    public void setSurviveTime(Long surviveTime) {
        this.surviveTime = surviveTime;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    @Override
    public String toString() {
        return "XiCi代理IP：" + ip + ":" + port + ", " + location + ", " + privacyType + ", " + proxyType
                + ", 延迟" + lag + "秒, 连接时间" + connectTime + "秒, 存活" + surviveTime + "分钟, 验证时间" + F.format(verifyTime);
    }
}
