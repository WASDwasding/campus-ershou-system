package com.campus.ershou.common.web;

import com.campus.ershou.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class NotImplemented {

    private NotImplemented() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(String message) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ApiResponse.fail(501, message));
    }
}
