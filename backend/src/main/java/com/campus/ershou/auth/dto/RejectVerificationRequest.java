package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectVerificationRequest(
        @NotBlank(message = "驳回原因不能为空")
        @Size(min = 2, max = 100, message = "驳回原因须为 2–100 字")
        String reason
) {
    public RejectVerificationRequest {
        reason = reason == null ? null : reason.trim();
    }
}
