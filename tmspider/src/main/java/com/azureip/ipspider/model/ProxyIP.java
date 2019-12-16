package com.azureip.ipspider.model;

import java.util.Date;

public class ProxyIP {
    // IP
    private String ip;
    // 端口号
    private Integer port;
    // 类型（0-未知；1-HTTP；2-HTTPS；3-SOCKET；4-其他）
    private Integer type;
    // 可用标记
    private Boolean available;
    // 延迟
    private Integer lag;
    // 抓取/入库时间
    private Date fetchTime;
    // 验证时间
    private Date verifyTime;
    // 失败时间（使用时连接失败）
    private Date voidTime;
    // 无效次数
    private Integer invalidTimes;
    // 废弃标记
    private Boolean discarded;

    public ProxyIP() {
    }

    public ProxyIP(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.type = 0;
        this.available = false;
        this.fetchTime = new Date();
        this.invalidTimes = 0;
        this.discarded = false;
    }

    public ProxyIP(String ip, Integer port, Integer type) {
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.available = false;
        this.fetchTime = new Date();
        this.invalidTimes = 0;
        this.discarded = false;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getLag() {
        return lag;
    }

    public void setLag(Integer lag) {
        this.lag = lag;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    public Integer getInvalidTimes() {
        return invalidTimes;
    }

    public void setInvalidTimes(Integer invalidTimes) {
        this.invalidTimes = invalidTimes;
    }

    public Boolean getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Boolean discarded) {
        this.discarded = discarded;
    }

    public Date getVoidTime() {
        return voidTime;
    }

    public void setVoidTime(Date voidTime) {
        this.voidTime = voidTime;
    }

    @Override
    public String toString() {
        return ip + ":" + port + ", type=" + type + ", available=" + available + ", lag=" + lag
                + ", fetchTime=" + (fetchTime == null ? "" : fetchTime.toLocaleString())
                + ", verifyTime=" + (verifyTime == null ? "" : verifyTime.toLocaleString())
                + ", voidTime=" + (voidTime == null ? "" : voidTime.toLocaleString())
                + ", invalidTimes=" + invalidTimes + ", discarded=" + discarded;
    }
}