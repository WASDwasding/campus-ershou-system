package com.campus.ershou.user;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.web.ClientIp;
import com.campus.ershou.user.dto.BanUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/api/admin/users")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String verifyStatus,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(adminUserService.list(keyword, status, verifyStatus, page, size));
    }

    @GetMapping("/api/admin/users/{userId}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long userId
    ) {
        return ApiResponse.ok(adminUserService.detail(userId));
    }

    @PostMapping("/api/admin/users/{userId}/ban")
    public ApiResponse<Map<String, Object>> ban(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long userId,
            @RequestBody BanUserRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(adminUserService.ban(
                loginAdmin.adminId(), userId, request.reason(), request.days(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/admin/users/{userId}/unban")
    public ApiResponse<Map<String, Object>> unban(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long userId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(adminUserService.unban(loginAdmin.adminId(), userId, ClientIp.resolve(httpRequest)));
    }
}
