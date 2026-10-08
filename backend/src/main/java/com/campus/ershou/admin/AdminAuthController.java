package com.campus.ershou.admin;

import com.campus.ershou.admin.dto.AdminLoginRequest;
import com.campus.ershou.admin.dto.AdminPasswordRequest;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/api/admin/auth/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.ok(adminAuthService.login(request));
    }

    @PostMapping("/api/admin/auth/logout")
    public ApiResponse<Map<String, Object>> logout(@AuthenticationPrincipal LoginAdmin loginAdmin) {
        adminAuthService.logout(loginAdmin);
        return ApiResponse.ok();
    }

    @GetMapping("/api/admin/me")
    public ApiResponse<Map<String, Object>> me(@AuthenticationPrincipal LoginAdmin loginAdmin) {
        return ApiResponse.ok(adminAuthService.me(loginAdmin));
    }

    @PostMapping("/api/admin/auth/password")
    public ApiResponse<Map<String, Object>> password(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @Valid @RequestBody AdminPasswordRequest request
    ) {
        adminAuthService.changePassword(loginAdmin, request);
        return ApiResponse.ok();
    }
}
