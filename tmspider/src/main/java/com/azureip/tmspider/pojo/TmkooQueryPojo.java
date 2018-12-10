package com.azureip.tmspider.pojo;

public class TmkooQueryPojo {

    // 排序依据-申请人
    private String orderBy = "SQR";
    // 排序方式-升序
    private String order = "ASC";
    // 列表展示方式：默认1
    private String d = "1";
    // Session的Key值
    private String l;
    // 流程状态
    private String lczt;
    // 分类-全部
    private String gjfl;
    // 类似群组：默认空
    private String qzhs;
    // 商品：默认空
    private String goods;
    // 初审公告期号-起始
    private String csggqhStart;
    // 初审公告期号-结束
    private String csggqhEnd;
    // 注册公告期号-起始
    private String zcggqhStart;
    // 注册公告期号-结束
    private String zcggqhEnd;
    // 初审公告日期-起始
    private String csggrqStart;
    // 初审公告日期-结束
    private String csggrqEnd;
    // 注册公告日期-起始
    private String zcggrqStart;
    // 注册公告日期-结束
    private String zcggrqEnd;
    // 申请日期-起始
    private String sqrqStart;
    // 申请日期-起始
    private String sqrqEnd;
    // 申请人[中文]
    private String sqrmcZw;
    // 代理机构
    private String dlrmc;
    // 申请人地址[中文]
    private String sqrdzZw;
    // 页码
    private String pageNo = "1";

    @Override
    public String toString() {
        return "TmkooQueryPojo{" +
                "orderBy='" + orderBy + '\'' +
                ", order='" + order + '\'' +
                ", d='" + d + '\'' +
                ", l='" + l + '\'' +
                ", lczt='" + lczt + '\'' +
                ", gjfl='" + gjfl + '\'' +
                ", qzhs='" + qzhs + '\'' +
                ", goods='" + goods + '\'' +
                ", csggqhStart='" + csggqhStart + '\'' +
                ", csggqhEnd='" + csggqhEnd + '\'' +
                ", zcggqhStart='" + zcggqhStart + '\'' +
                ", zcggqhEnd='" + zcggqhEnd + '\'' +
                ", csggrqStart='" + csggrqStart + '\'' +
                ", csggrqEnd='" + csggrqEnd + '\'' +
                ", zcggrqStart='" + zcggrqStart + '\'' +
                ", zcggrqEnd='" + zcggrqEnd + '\'' +
                ", sqrqStart='" + sqrqStart + '\'' +
                ", sqrqEnd='" + sqrqEnd + '\'' +
                ", sqrmcZw='" + sqrmcZw + '\'' +
                ", dlrmc='" + dlrmc + '\'' +
                ", sqrdzZw='" + sqrdzZw + '\'' +
                ", pageNo=" + pageNo +
                '}';
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getLczt() {
        return lczt;
    }

    public void setLczt(String lczt) {
        this.lczt = lczt;
    }

    public String getGjfl() {
        return gjfl;
    }

    public void setGjfl(String gjfl) {
        this.gjfl = gjfl;
    }

    public String getQzhs() {
        return qzhs;
    }

    public void setQzhs(String qzhs) {
        this.qzhs = qzhs;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getCsggqhStart() {
        return csggqhStart;
    }

    public void setCsggqhStart(String csggqhStart) {
        this.csggqhStart = csggqhStart;
    }

    public String getCsggqhEnd() {
        return csggqhEnd;
    }

    public void setCsggqhEnd(String csggqhEnd) {
        this.csggqhEnd = csggqhEnd;
    }

    public String getZcggqhStart() {
        return zcggqhStart;
    }

    public void setZcggqhStart(String zcggqhStart) {
        this.zcggqhStart = zcggqhStart;
    }

    public String getZcggqhEnd() {
        return zcggqhEnd;
    }

    public void setZcggqhEnd(String zcggqhEnd) {
        this.zcggqhEnd = zcggqhEnd;
    }

    public String getCsggrqStart() {
        return csggrqStart;
    }

    public void setCsggrqStart(String csggrqStart) {
        this.csggrqStart = csggrqStart;
    }

    public String getCsggrqEnd() {
        return csggrqEnd;
    }

    public void setCsggrqEnd(String csggrqEnd) {
        this.csggrqEnd = csggrqEnd;
    }

    public String getZcggrqStart() {
        return zcggrqStart;
    }

    public void setZcggrqStart(String zcggrqStart) {
        this.zcggrqStart = zcggrqStart;
    }

    public String getZcggrqEnd() {
        return zcggrqEnd;
    }

    public void setZcggrqEnd(String zcggrqEnd) {
        this.zcggrqEnd = zcggrqEnd;
    }

    public String getSqrqStart() {
        return sqrqStart;
    }

    public void setSqrqStart(String sqrqStart) {
        this.sqrqStart = sqrqStart;
    }

    public String getSqrqEnd() {
        return sqrqEnd;
    }

    public void setSqrqEnd(String sqrqEnd) {
        this.sqrqEnd = sqrqEnd;
    }

    public String getSqrmcZw() {
        return sqrmcZw;
    }

    public void setSqrmcZw(String sqrmcZw) {
        this.sqrmcZw = sqrmcZw;
    }

    public String getDlrmc() {
        return dlrmc;
    }

    public void setDlrmc(String dlrmc) {
        this.dlrmc = dlrmc;
    }

    public String getSqrdzZw() {
        return sqrdzZw;
    }

    public void setSqrdzZw(String sqrdzZw) {
        this.sqrdzZw = sqrdzZw;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }
}
