package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.List;

public class AnnoucementListPojo implements Serializable {

    public AnnoucementListPojo(int total, List<AnnoucementPojo> rows) {
        this.total = total;
        this.rows = rows;
    }

    // 公告总数
    private int total;
    // 公告列表
    private List<AnnoucementPojo> rows;

    public int getTotal() {
        return total;
    }

    public List<AnnoucementPojo> getRows() {
        return rows;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setRows(List<AnnoucementPojo> rows) {
        this.rows = rows;
    }
}
