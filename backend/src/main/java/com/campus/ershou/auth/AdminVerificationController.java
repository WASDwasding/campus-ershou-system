package com.campus.ershou.auth;

import com.campus.ershou.auth.dto.ApproveVerificationRequest;
import com.campus.ershou.auth.dto.RejectVerificationRequest;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.web.ClientIp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminVerificationController {

    private final AdminVerificationService adminVerificationService;

    public AdminVerificationController(AdminVerificationService adminVerificationService) {
        this.adminVerificationService = adminVerificationService;
    }

    @GetMapping("/api/admin/verifications")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(adminVerificationService.list(status, page, size));
    }

    @GetMapping("/api/admin/verifications/{id}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id
    ) {
        return ApiResponse.ok(adminVerificationService.detail(id));
    }

    @PostMapping("/api/admin/verifications/{id}/approve")
    public ApiResponse<Map<String, Object>> approve(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @RequestBody(required = false) ApproveVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(adminVerificationService.approve(loginAdmin.adminId(), id, ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/admin/verifications/{id}/reject")
    public ApiResponse<Map<String, Object>> reject(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @Valid @RequestBody RejectVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(adminVerificationService.reject(
                loginAdmin.adminId(), id, request.reason(), ClientIp.resolve(httpRequest)));
    }
}
