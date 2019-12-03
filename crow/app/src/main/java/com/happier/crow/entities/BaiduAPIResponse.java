package com.happier.crow.entities;

public class BaiduAPIResponse {

    private int status;
    private Result result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "BaiduAPIResponse{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
