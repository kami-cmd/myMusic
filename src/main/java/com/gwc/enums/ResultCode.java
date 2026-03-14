package com.gwc.enums;

/**
 * 返回状态码枚举
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),
    ACCEPTED(202, "请求已接受"),
    NO_CONTENT(204, "操作成功，无返回内容"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务相关错误码
    BUSINESS_ERROR(1000, "业务异常"),
    VALIDATE_FAILED(1001, "参数校验失败"),
    UPLOAD_FAILED(1002, "文件上传失败"),
    DATA_NOT_EXIST(1003, "数据不存在"),
    DATA_EXISTED(1004, "数据已存在"),
    ERROR(444,"错误" );

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}