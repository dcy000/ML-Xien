package com.example.han.referralproject.bean;

public class YuYueInfo {

    public String start_time;

    public String end_time;

    public String state;

    public int rid;

    public YuYueInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public YuYueInfo(String start_time, String end_time, String state, int rid) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.state = state;
        this.rid = rid;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "YuYueInfo{" + "start_time='" + start_time + '\'' + ", end_time='" + end_time + '\'' + ", state='" + state + '\'' + ", rid=" + rid + '}';
    }
}
