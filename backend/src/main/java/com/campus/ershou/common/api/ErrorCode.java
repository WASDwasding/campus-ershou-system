package com.campus.ershou.common.api;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    OK(HttpStatus.OK, "ok"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "请求格式错误"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "字段不合法"),
    UNKNOWN_FIELD(HttpStatus.BAD_REQUEST, "请求体含未定义字段"),
    CONTACT_INFO_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "文本含联系方式"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "未登录或 token 无效"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "学号或密码错误"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "没有权限"),
    NOT_VERIFIED(HttpStatus.FORBIDDEN, "需要校园认证"),
    USER_BANNED(HttpStatus.FORBIDDEN, "账号已被封禁"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "资源不存在"),
    CONFLICT(HttpStatus.CONFLICT, "状态冲突"),
    STUDENT_NO_TAKEN(HttpStatus.CONFLICT, "学号已被绑定"),
    USERNAME_TAKEN(HttpStatus.CONFLICT, "用户名已被占用"),
    EMAIL_TAKEN(HttpStatus.CONFLICT, "邮箱已被注册"),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "验证码错误"),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "验证码已过期，请重新获取"),
    SETUP_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "设置密码凭证无效或已过期"),
    FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "不是 JPEG、PNG 或 WebP"),
    FILE_NOT_USABLE(HttpStatus.BAD_REQUEST, "文件不可用"),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "文件过大"),
    ALREADY_VERIFIED(HttpStatus.CONFLICT, "已经认证"),
    VERIFICATION_PENDING(HttpStatus.CONFLICT, "认证审核中"),
    VERIFICATION_NOT_PENDING(HttpStatus.CONFLICT, "认证申请已被处理"),
    PASSWORD_CHANGE_REQUIRED(HttpStatus.FORBIDDEN, "管理员须先修改初始密码"),
    GOODS_NOT_EDITABLE(HttpStatus.CONFLICT, "当前商品状态不能修改"),
    GOODS_NOT_AVAILABLE(HttpStatus.CONFLICT, "商品不可购买"),
    CANNOT_BUY_OWN_GOODS(HttpStatus.CONFLICT, "不能买自己的商品"),
    PENDING_ORDER_LIMIT(HttpStatus.CONFLICT, "待支付订单已达上限"),
    ORDER_STATUS_INVALID(HttpStatus.CONFLICT, "订单状态不允许该操作"),
    ORDER_EXPIRED(HttpStatus.CONFLICT, "订单已超时"),
    ORDER_RESTRICTED(HttpStatus.FORBIDDEN, "超时未付次数过多"),
    PAYMENT_NOT_PAYABLE(HttpStatus.CONFLICT, "支付单不可付款"),
    PAY_CHANNEL_UNSUPPORTED(HttpStatus.BAD_REQUEST, "不支持的支付渠道"),
    DISPUTE_NOT_OPEN(HttpStatus.CONFLICT, "没有进行中的申诉"),
    ALREADY_REVIEWED(HttpStatus.CONFLICT, "已评价"),
    REVIEW_WINDOW_CLOSED(HttpStatus.CONFLICT, "超过评价期"),
    CANNOT_CHAT_WITH_SELF(HttpStatus.CONFLICT, "不能和自己聊天"),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "24 小时内已举报该目标"),
    GOODS_IN_DISPUTE(HttpStatus.CONFLICT, "商品订单在申诉中"),
    REPORT_ALREADY_HANDLED(HttpStatus.CONFLICT, "举报已处理"),
    RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁"),
    LOGIN_LOCKED(HttpStatus.TOO_MANY_REQUESTS, "登录失败次数过多，请稍后再试"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "服务器错误"),
    UPSTREAM_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "上游服务不可用");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
