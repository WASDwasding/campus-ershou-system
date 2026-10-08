package com.campus.ershou.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 32, message = "用户名过长")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(max = 64, message = "密码不合法")
        String password
) {
    public AdminLoginRequest {
        username = username == null ? null : username.trim();
    }
}
