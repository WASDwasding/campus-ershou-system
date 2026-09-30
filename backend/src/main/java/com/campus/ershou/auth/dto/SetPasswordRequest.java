package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequest(
        @NotBlank(message = "设置密码凭证不能为空")
        @Size(min = 16, max = 128, message = "设置密码凭证无效")
        String setupToken,

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, max = 64, message = "密码长度须为 8–64 位")
        String password
) {
    public SetPasswordRequest {
        setupToken = setupToken == null ? null : setupToken.trim();
    }
}
