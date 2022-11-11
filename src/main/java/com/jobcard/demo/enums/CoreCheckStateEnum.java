package com.jobcard.demo.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum CoreCheckStateEnum {
    S_1("-1", "无需校验",true),
    S0("0", "正常",true),
    S1("1", "卡已绑定",false),
    S2("2", "人已发卡",false),
//    S3("3", "占用",false),
     S98("98", "请求后台异常",false),
    S99("99", "后台异常Code",false),
    ;
    private String code;
    private String name;

    private boolean isUsable;
    private static Map<String, CoreCheckStateEnum> mappingMap;

    static {
        mappingMap = Arrays.stream(CoreCheckStateEnum.values()).collect(Collectors.toMap(o -> o.getCode(), o -> o));
    }

    CoreCheckStateEnum(String code, String name,boolean isUsable) {
        this.code = code;
        this.name = name;
        this.isUsable = isUsable;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public static CoreCheckStateEnum getEnumByCode(String code) {
        CoreCheckStateEnum coreCheckStateEnum = mappingMap.get(code);
        if (Objects.isNull(coreCheckStateEnum)) {
            return S99;
        }
        return coreCheckStateEnum;
    }

    @Override
    public String toString() {
        return "CoreCheckStateEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", isUsable=" + isUsable +
                '}';
    }
}
