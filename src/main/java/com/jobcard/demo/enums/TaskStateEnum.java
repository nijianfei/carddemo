package com.jobcard.demo.enums;


public enum TaskStateEnum {
    READY("-1","未处理"),
    BUSY("0","处理中"),
    SUCC("1","成功"),
    FAIL("2","失败"),
    RETRY("3","状态未知，重试"),
    ;

    private String code;
    private String value;

    TaskStateEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
