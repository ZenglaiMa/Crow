package com.happier.crow.entities;

import java.util.Date;

public class Children {

    private int cid;
    private String phone;
    private String name;
    private String password;
    private int gender;
    private int age;
    private Date birth;
    private String iconPath;
    private String profile;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "Children [cid=" + cid + ", phone=" + phone + ", name=" + name + ", password=" + password + ", gender="
                + gender + ", age=" + age + ", birth=" + birth + ", iconPath=" + iconPath + ", profile=" + profile
                + "]";
    }

}
