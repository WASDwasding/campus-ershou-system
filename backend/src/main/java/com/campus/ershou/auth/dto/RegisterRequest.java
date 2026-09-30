package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "学号不能为空")
        @Pattern(regexp = "^\\d{8,12}$", message = "学号须为 8–12 位数字")
        String studentNo,

        @NotBlank(message = "用户名不能为空")
        @Size(min = 1, max = 16, message = "用户名长度为 1–16 个字符")
        @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "用户名仅支持中文、字母、数字和下划线")
        String username,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱过长")
        String email
) {
    public RegisterRequest {
        studentNo = studentNo == null ? null : studentNo.trim();
        username = username == null ? null : username.trim();
        email = email == null ? null : email.trim().toLowerCase();
    }
}
