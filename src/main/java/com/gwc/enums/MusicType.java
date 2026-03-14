package com.gwc.enums;

public enum MusicType {
    UNCLEARED(0,"未知"),
    ROCK(1,"摇滚"),
    POP(2,"流行");


    private  int code;
    private String type;

    MusicType(int code, String type) {
        this.code = code;
        this.type = type;
    }
}
