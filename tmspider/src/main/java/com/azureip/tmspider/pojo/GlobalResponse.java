package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.List;

public class GlobalResponse<T> implements Serializable {

    public static final String SUCCESS = "S";
    public static final String ERROR = "E";

    public GlobalResponse() {
    }

    public GlobalResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // S - 成功；E - 失败。
    private String status;
    private String message;
    private T result;
    private List<T> resultList;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }
}
