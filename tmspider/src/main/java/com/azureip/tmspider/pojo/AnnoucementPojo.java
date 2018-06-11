package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.Date;

public class AnnoucementPojo implements Serializable {

    public AnnoucementPojo(String id, int page_no, String ann_type_code, String ann_type, String ann_num, Date ann_date, String reg_name, String reg_num, String tm_name) {
        this.id = id;
        this.page_no = page_no;
        this.ann_type_code = ann_type_code;
        this.ann_type = ann_type;
        this.ann_num = ann_num;
        this.ann_date = ann_date;
        this.reg_name = reg_name;
        this.reg_num = reg_num;
        this.tm_name = tm_name;
    }

    // 主键ID
    private String id;
    // 页码
    private int page_no;
    // 公告类型代码
    private String ann_type_code;
    // 公告类型
    private String ann_type;
    // 公告期号
    private String ann_num;
    // 公告日期
    private Date ann_date;
    // 申请人
    private String reg_name;
    // 注册号
    private String reg_num;
    // 商标名称
    private String tm_name;

    public String getId() {
        return id;
    }

    public int getPage_no() {
        return page_no;
    }

    public String getAnn_type_code() {
        return ann_type_code;
    }

    public String getAnn_type() {
        return ann_type;
    }

    public String getAnn_num() {
        return ann_num;
    }

    public Date getAnn_date() {
        return ann_date;
    }

    public String getReg_name() {
        return reg_name;
    }

    public String getReg_num() {
        return reg_num;
    }

    public String getTm_name() {
        return tm_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public void setAnn_type_code(String ann_type_code) {
        this.ann_type_code = ann_type_code;
    }

    public void setAnn_type(String ann_type) {
        this.ann_type = ann_type;
    }

    public void setAnn_num(String ann_num) {
        this.ann_num = ann_num;
    }

    public void setAnn_date(Date ann_date) {
        this.ann_date = ann_date;
    }

    public void setReg_name(String reg_name) {
        this.reg_name = reg_name;
    }

    public void setReg_num(String reg_num) {
        this.reg_num = reg_num;
    }

    public void setTm_name(String tm_name) {
        this.tm_name = tm_name;
    }

    @Override
    public String toString() {
        return "AnnoucementPojo{" +
                "ann_num='" + ann_num + '\'' +
                ", ann_date=" + ann_date +
                ", reg_name='" + reg_name + '\'' +
                ", reg_num='" + reg_num + '\'' +
                ", tm_name='" + tm_name + '\'' +
                '}';
    }
}
