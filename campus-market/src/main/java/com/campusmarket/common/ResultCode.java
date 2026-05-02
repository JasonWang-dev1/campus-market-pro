package com.campusmarket.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(400, "操作失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    /* 业务错误码 1001+ */
    PARAM_ERROR(1001, "参数错误"),
    USER_EXISTS(1002, "用户已存在"),
    USER_NOT_FOUND(1003, "用户不存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    PHONE_EXISTS(1005, "手机号已被注册"),
    FILE_TOO_LARGE(1006, "文件过大"),
    FILE_TYPE_ERROR(1007, "文件类型不支持");

    private final int code;
    private final String message;
}
