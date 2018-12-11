package com.azureip.tmspider.model;

import java.util.Date;

public class TMKooRecord {
    private String regNum;

    private String tmName;

    private Integer tmType;

    private String appName;

    private Date appDate;

    private String appAddress;

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum == null ? null : regNum.trim();
    }

    public String getTmName() {
        return tmName;
    }

    public void setTmName(String tmName) {
        this.tmName = tmName == null ? null : tmName.trim();
    }

    public Integer getTmType() {
        return tmType;
    }

    public void setTmType(Integer tmType) {
        this.tmType = tmType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }

    public String getAppAddress() {
        return appAddress;
    }

    public void setAppAddress(String appAddress) {
        this.appAddress = appAddress == null ? null : appAddress.trim();
    }
}