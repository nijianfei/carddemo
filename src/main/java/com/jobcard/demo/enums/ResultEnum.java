package com.jobcard.demo.enums;

public enum ResultEnum implements ResulltCode{
    SUCCESS("70","成功"),

    ;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
