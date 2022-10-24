package com.jobcard.demo.enums;

//execStatusCls：  0开始刷卡 1刷卡成功 2 刷卡失败
public enum DeviceStateEnum {
    FREE("-2","空闲",""),
    READY("-1","就緒",""),
    BUSY("0","开始刷卡",""),
    SUCC("1","刷卡成功",""),
    FAIL("2","刷卡失败",""),
    ;

    private String code;
    private String value;
    private String detailMsg;

    DeviceStateEnum(String code, String value ,String detailMsg) {
        this.code = code;
        this.value = value;
        this.detailMsg = detailMsg;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDetailMsg() {
        return detailMsg;
    }

    public DeviceStateEnum setDetailMsg(String detailMsg) {
        this.detailMsg = detailMsg;
        return this;
    }

    @Override
    public String toString() {
        return "DeviceStateEnum{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", detailMsg='" + detailMsg + '\'' +
                '}';
    }
}
