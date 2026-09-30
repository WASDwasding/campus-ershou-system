package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "学号不能为空")
        @Pattern(regexp = "^\\d{8,12}$", message = "学号须为 8–12 位数字")
        String studentNo,

        @NotBlank(message = "密码不能为空")
        @Size(min = 1, max = 64, message = "密码不合法")
        String password
) {
    public LoginRequest {
        studentNo = studentNo == null ? null : studentNo.trim();
    }
}
