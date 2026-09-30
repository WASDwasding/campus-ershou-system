package com.campus.ershou.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, ErrorCode errorCode, Object data) throws IOException {
        write(response, errorCode, errorCode.getDefaultMessage(), data, null);
    }

    public void write(HttpServletResponse response, ApiException ex) throws IOException {
        write(response, ex.getErrorCode(), ex.getMessage(), ex.getData(), ex.getRetryAfterSeconds());
    }

    public void write(HttpServletResponse response, ErrorCode errorCode, String message, Object data, Long retryAfterSeconds)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (retryAfterSeconds != null) {
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        }
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.fail(errorCode, message, data));
    }
}
