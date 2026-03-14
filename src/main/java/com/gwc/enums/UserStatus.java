package com.gwc.enums;

public enum UserStatus {
    FREEZE(0, "冻结"),
    NORMAL(1, "正常");

    private Integer code;
    private String descrption;

    UserStatus(Integer code, String descrption) {
        this.code = code;
        this.descrption = descrption;
    }

    public Integer getCode() {
        return code;
    }
}
