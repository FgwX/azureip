package com.azureip.tmspider.pojo;

import com.azureip.tmspider.model.Announcement;

import java.io.Serializable;
import java.util.List;

public class AnnListPojo implements Serializable {

    public AnnListPojo(int total, List<Announcement> rows) {
        this.total = total;
        this.rows = rows;
    }

    // 公告总数
    private int total;
    // 公告列表
    private List<Announcement> rows;

    public int getTotal() {
        return total;
    }

    public List<Announcement> getRows() {
        return rows;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setRows(List<Announcement> rows) {
        this.rows = rows;
    }
}
