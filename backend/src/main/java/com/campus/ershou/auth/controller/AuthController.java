package com.campus.ershou.auth.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register() {
        return NotImplemented.of("注册尚未实现");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login() {
        return NotImplemented.of("登录尚未实现");
    }
}
