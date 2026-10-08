package com.campus.ershou.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminPasswordRequest(
        @NotBlank(message = "原密码不能为空")
        String oldPassword,

        @NotBlank(message = "新密码不能为空")
        @Size(min = 12, max = 64, message = "新密码长度须为 12–64 位")
        String newPassword
) {
}
