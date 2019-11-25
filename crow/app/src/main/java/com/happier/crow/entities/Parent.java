package com.happier.crow.entities;

public class Parent {

    private int pid;
    private String phone;
    private String name;
    private String password;
    private int gender;
    private int age;
    private String iconPath;
    private String province;
    private String city;
    private String area;
    private String detailAddress;
    private int registerId;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public int getRegisterId() {
        return registerId;
    }

    public void setRegisterId(int registerId) {
        this.registerId = registerId;
    }

    @Override
    public String toString() {
        return "Parent [pid=" + pid + ", phone=" + phone + ", name=" + name + ", password=" + password + ", gender="
                + gender + ", age=" + age + ", iconPath=" + iconPath + ", province=" + province + ", city=" + city
                + ", area=" + area + ", detailAddress=" + detailAddress + ", registerId=" + registerId + "]";
    }

}
