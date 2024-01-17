package com.jobcard.demo.bean;

import com.jobcard.demo.enums.DeviceStateEnum;
import dcrf.JavaRD800;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeviceState {
    private DeviceStateEnum stateEnum;
    private String lastCardNo;
    private String msg;
    private JavaRD800 rd;
    private String userId;

    private String lastRdCardNo;

    public DeviceStateEnum getStateEnum() {
        return stateEnum;
    }

    public void setStateEnum(DeviceStateEnum stateEnum) {
        this.stateEnum = stateEnum;
    }

    public String getLastCardNo() {
        return lastCardNo;
    }

    public void setLastCardNo(String lastCardNo) {
        this.lastCardNo = lastCardNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JavaRD800 getRd() {
        return rd;
    }

    public void setRd(JavaRD800 rd) {
        this.rd = rd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getLastRdCardNo() {
        return lastRdCardNo;
    }

    public void setLastRdCardNo(String lastRdCardNo) {
        this.lastRdCardNo = lastRdCardNo;
    }
}
