package com.gwc.enums;



public enum UserLevel {

    Normal(1, "普通用户"),
    Vip(2, "vip用户"),
    Svip(3, "超级vip用户");

    private Integer code;
    private String level;

    UserLevel(int code, String level) {
        this.code = code;
        this.level = level;
    }

    public Integer getCode() {
        return code;
    }
}
