package com.example.calllog;

import com.google.gson.annotations.SerializedName;

public class CallList {
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("time")
    private String time;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
