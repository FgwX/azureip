package com.azureip.ipspider.model;

import java.util.Date;

public class ProxyIPModel extends ProxyIPModelKey {
    private String type;

    private Boolean available;

    private Float lag;

    private Date fetchTime;

    private Date verifyTime;

    private Boolean invalidTimes;

    private Boolean discarded;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Float getLag() {
        return lag;
    }

    public void setLag(Float lag) {
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

    public Boolean getInvalidTimes() {
        return invalidTimes;
    }

    public void setInvalidTimes(Boolean invalidTimes) {
        this.invalidTimes = invalidTimes;
    }

    public Boolean getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Boolean discarded) {
        this.discarded = discarded;
    }
}