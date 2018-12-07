package com.azureip.tmspider.model;

import java.util.Date;

public class ExcelOptRecord {
    private String fileName;

    private Date optTime;

    private Integer totalCount;

    private Integer existFirstTrialCount;

    private Integer markFirstTrialCount;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getExistFirstTrialCount() {
        return existFirstTrialCount;
    }

    public void setExistFirstTrialCount(Integer existFirstTrialCount) {
        this.existFirstTrialCount = existFirstTrialCount;
    }

    public Integer getMarkFirstTrialCount() {
        return markFirstTrialCount;
    }

    public void setMarkFirstTrialCount(Integer markFirstTrialCount) {
        this.markFirstTrialCount = markFirstTrialCount;
    }
}