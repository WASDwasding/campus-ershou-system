package com.campus.ershou.auth;

import com.campus.ershou.auth.dto.CampusVerificationRequest;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CampusVerificationController {

    private final CampusVerificationService verificationService;

    public CampusVerificationController(CampusVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/api/auth/campus-verification")
    public ApiResponse<Map<String, Object>> current(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(verificationService.current(loginUser.userId()));
    }

    @PostMapping("/api/auth/campus-verification")
    public ApiResponse<Map<String, Object>> submit(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CampusVerificationRequest request
    ) {
        return ApiResponse.ok(verificationService.submit(loginUser.userId(), request));
    }
}
