package com.campus.ershou.common.api;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Set<String> TAMPER_FIELDS = Set.of(
            "price", "amount", "paid", "status", "stock",
            "sellerId", "buyerId", "userId", "role"
    );

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApi(ApiException ex) {
        return toResponse(ex.getErrorCode(), ex.getMessage(), ex.getData(), ex.getRetryAfterSeconds());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValid(MethodArgumentNotValidException ex) {
        List<FieldErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toItem)
                .toList();
        ApiException api = ApiException.validation(errors);
        return toResponse(api.getErrorCode(), api.getMessage(), api.getData(), null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException ex) {
        List<FieldErrorItem> errors = ex.getConstraintViolations().stream()
                .map(v -> new FieldErrorItem(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        ApiException api = ApiException.validation(errors);
        return toResponse(api.getErrorCode(), api.getMessage(), api.getData(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof UnrecognizedPropertyException unknown) {
            String field = unknown.getPropertyName();
            if (TAMPER_FIELDS.contains(field)) {
                log.warn("TAMPER_FIELD field={} ip={} path={}", field, request.getRemoteAddr(), request.getRequestURI());
            }
            return toResponse(
                    ErrorCode.UNKNOWN_FIELD,
                    ErrorCode.UNKNOWN_FIELD.getDefaultMessage(),
                    java.util.Map.of("fieldErrors", List.of(new FieldErrorItem(field, "未知字段"))),
                    null
            );
        }
        if (cause instanceof InvalidFormatException || cause instanceof MismatchedInputException) {
            return toResponse(ErrorCode.BAD_REQUEST, "JSON 或类型错误", java.util.Map.of(), null);
        }
        return toResponse(ErrorCode.BAD_REQUEST, "JSON 或类型错误", java.util.Map.of(), null);
    }

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFound() {
        return toResponse(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getDefaultMessage(), java.util.Map.of(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethod() {
        return toResponse(ErrorCode.BAD_REQUEST, "请求方法不受支持", java.util.Map.of(), null);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleParam() {
        return toResponse(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getDefaultMessage(), java.util.Map.of(), null);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate() {
        return toResponse(ErrorCode.CONFLICT, ErrorCode.CONFLICT.getDefaultMessage(), java.util.Map.of(), null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuth() {
        return toResponse(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getDefaultMessage(), java.util.Map.of(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleDenied() {
        return toResponse(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getDefaultMessage(), java.util.Map.of(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnknown(Exception ex) {
        log.error("unhandled error", ex);
        return toResponse(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage(), java.util.Map.of(), null);
    }

    private static FieldErrorItem toItem(FieldError error) {
        String message = error.getDefaultMessage() == null ? "不合法" : error.getDefaultMessage();
        return new FieldErrorItem(error.getField(), message);
    }

    private static ResponseEntity<ApiResponse<Object>> toResponse(
            ErrorCode code, String message, Object data, Long retryAfterSeconds) {
        HttpHeaders headers = new HttpHeaders();
        if (retryAfterSeconds != null) {
            headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        }
        return new ResponseEntity<>(ApiResponse.fail(code, message, data), headers, code.getHttpStatus());
    }
}
