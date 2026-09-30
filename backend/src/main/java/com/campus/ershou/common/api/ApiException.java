package com.campus.ershou.common.api;

import java.util.List;
import java.util.Map;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object data;
    private final Long retryAfterSeconds;

    public ApiException(ErrorCode errorCode, String message, Object data, Long retryAfterSeconds) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public static ApiException of(ErrorCode errorCode) {
        return new ApiException(errorCode, errorCode.getDefaultMessage(), Map.of(), null);
    }

    public static ApiException of(ErrorCode errorCode, String message) {
        return new ApiException(errorCode, message, Map.of(), null);
    }

    public static ApiException of(ErrorCode errorCode, Object data) {
        return new ApiException(errorCode, errorCode.getDefaultMessage(), data, null);
    }

    public static ApiException validation(List<FieldErrorItem> fieldErrors) {
        return new ApiException(
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                Map.of("fieldErrors", fieldErrors),
                null
        );
    }

    public static ApiException rateLimited(long retryAfterSeconds) {
        return new ApiException(
                ErrorCode.RATE_LIMITED,
                ErrorCode.RATE_LIMITED.getDefaultMessage(),
                Map.of(),
                retryAfterSeconds
        );
    }

    public static ApiException loginLocked(long retryAfterSeconds) {
        return new ApiException(
                ErrorCode.LOGIN_LOCKED,
                ErrorCode.LOGIN_LOCKED.getDefaultMessage(),
                Map.of(),
                retryAfterSeconds
        );
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }

    public Long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
