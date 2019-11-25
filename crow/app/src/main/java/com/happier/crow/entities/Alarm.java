package com.happier.crow.entities;

import java.util.Date;

public class Alarm {

    private int id;
    private String type;
    private Date time;
    private int state;
    private String description;
    private int pid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "Alarm [id=" + id + ", type=" + type + ", time=" + time + ", state=" + state + ", description="
                + description + ", pid=" + pid + "]";
    }

}
