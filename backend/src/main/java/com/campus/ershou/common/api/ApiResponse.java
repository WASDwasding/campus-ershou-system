package com.campus.ershou.common.api;

import com.campus.ershou.common.web.TraceIdFilter;

import java.util.Map;

public record ApiResponse<T>(String code, String message, T data, String traceId) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "ok", data, TraceIdFilter.current());
    }

    public static ApiResponse<Map<String, Object>> ok() {
        return ok(Map.of());
    }

    public static ApiResponse<Object> fail(ErrorCode errorCode, Object data) {
        return fail(errorCode, errorCode.getDefaultMessage(), data);
    }

    public static ApiResponse<Object> fail(ErrorCode errorCode, String message, Object data) {
        Object payload = data == null ? Map.of() : data;
        return new ApiResponse<>(errorCode.name(), message, payload, TraceIdFilter.current());
    }
}
