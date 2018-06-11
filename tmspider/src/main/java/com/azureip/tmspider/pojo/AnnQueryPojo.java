package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.Date;

public class AnnQueryPojo implements Serializable {
    public AnnQueryPojo() {
    }

    public AnnQueryPojo(int page, int rows, int annNum, String annType, boolean totalYOrN, String appDateBegin, String appDateEnd, int total) {
        this.page = page;
        this.rows = rows;
        this.annNum = annNum;
        this.annType = annType;
        this.totalYOrN = totalYOrN;
        this.appDateBegin = appDateBegin;
        this.appDateEnd = appDateEnd;
        this.total = total;
    }

    private int page;
    private int rows;
    private int annNum;
    private String annType;
    private boolean totalYOrN = true;
    private String appDateBegin;
    private String appDateEnd;
    private int total;

    public int getPage() {
        return page;
    }

    public int getRows() {
        return rows;
    }

    public int getAnnNum() {
        return annNum;
    }

    public String getAnnType() {
        return annType;
    }

    public boolean isTotalYOrN() {
        return totalYOrN;
    }

    public String getAppDateBegin() {
        return appDateBegin;
    }

    public String getAppDateEnd() {
        return appDateEnd;
    }

    public int getTotal() {
        return total;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setAnnNum(int annNum) {
        this.annNum = annNum;
    }

    public void setAnnType(String annType) {
        this.annType = annType;
    }

    public void setTotalYOrN(boolean totalYOrN) {
        this.totalYOrN = totalYOrN;
    }

    public void setAppDateBegin(String appDateBegin) {
        this.appDateBegin = appDateBegin;
    }

    public void setAppDateEnd(String appDateEnd) {
        this.appDateEnd = appDateEnd;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
