package com.azureip.tmspider.model;

import java.util.Date;

public class Announcement {
    public Announcement() {
    }

    public Announcement(String id, Integer pageNo, String annTypeCode, String annType, String annNum, Date annDate, String regName, String regNum, String tmName) {
        this.id = id;
        this.pageNo = pageNo;
        this.annTypeCode = annTypeCode;
        this.annType = annType;
        this.annNum = annNum;
        this.annDate = annDate;
        this.regName = regName;
        this.regNum = regNum;
        this.tmName = tmName;
    }

    private String id;

    private Integer pageNo;

    private String annTypeCode;

    private String annType;

    private String annNum;

    private Date annDate;

    private String regName;

    private String regNum;

    private String tmName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getAnnTypeCode() {
        return annTypeCode;
    }

    public void setAnnTypeCode(String annTypeCode) {
        this.annTypeCode = annTypeCode == null ? null : annTypeCode.trim();
    }

    public String getAnnType() {
        return annType;
    }

    public void setAnnType(String annType) {
        this.annType = annType == null ? null : annType.trim();
    }

    public String getAnnNum() {
        return annNum;
    }

    public void setAnnNum(String annNum) {
        this.annNum = annNum == null ? null : annNum.trim();
    }

    public Date getAnnDate() {
        return annDate;
    }

    public void setAnnDate(Date annDate) {
        this.annDate = annDate;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName == null ? null : regName.trim();
    }

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

    @Override
    public String toString() {
        return "Announcement{" +
                "id='" + id + '\'' +
                ", pageNo=" + pageNo +
                ", annTypeCode='" + annTypeCode + '\'' +
                ", annType='" + annType + '\'' +
                ", annNum='" + annNum + '\'' +
                ", annDate=" + annDate +
                ", regName='" + regName + '\'' +
                ", regNum='" + regNum + '\'' +
                ", tmName='" + tmName + '\'' +
                '}';
    }
}