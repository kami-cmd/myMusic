package com.gwc.entity;

import com.gwc.enums.ResultCode;

import java.io.Serializable;

/**
 * 统一返回结果类
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;    // 状态码
    private String message;  // 返回消息
    private T data;          // 返回数据

    // 无参构造
    public Result() {
    }

    // 全参构造
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // getter和setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 常用静态方法
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> error() {
        return error(ResultCode.ERROR.getMessage());
    }

    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    // 链式调用方法
    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}