package com.happier.crow.entities;

public class Contact {

    private int id;
    private int adderStatus;
    private int adderId;
    private int addederId;
    private String remark;
    private int isIce;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdderStatus() {
        return adderStatus;
    }

    public void setAdderStatus(int adderStatus) {
        this.adderStatus = adderStatus;
    }

    public int getAdderId() {
        return adderId;
    }

    public void setAdderId(int adderId) {
        this.adderId = adderId;
    }

    public int getAddederId() {
        return addederId;
    }

    public void setAddederId(int addederId) {
        this.addederId = addederId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsIce() {
        return isIce;
    }

    public void setIsIce(int isIce) {
        this.isIce = isIce;
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", adderStatus=" + adderStatus + ", adderId=" + adderId + ", addederId="
                + addederId + ", remark=" + remark + ", isIce=" + isIce + "]";
    }

}
