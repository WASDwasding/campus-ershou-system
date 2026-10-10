package com.campus.ershou.auth;

import com.campus.ershou.auth.dto.DevLoginRequest;
import com.campus.ershou.auth.dto.AuthTokenResponse;
import com.campus.ershou.auth.dto.LoginRequest;
import com.campus.ershou.auth.dto.RegisterRequest;
import com.campus.ershou.auth.dto.RegisterSentResponse;
import com.campus.ershou.auth.dto.SetPasswordRequest;
import com.campus.ershou.auth.dto.SetupTokenResponse;
import com.campus.ershou.auth.dto.VerifyCodeRequest;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.web.ClientIp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth/register")
    public ApiResponse<RegisterSentResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(authService.register(request, ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/auth/register/verify-code")
    public ApiResponse<SetupTokenResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        return ApiResponse.ok(authService.verifyCode(request));
    }

    @PostMapping("/api/auth/register/set-password")
    public ApiResponse<AuthTokenResponse> setPassword(@Valid @RequestBody SetPasswordRequest request) {
        return ApiResponse.ok(authService.setPassword(request));
    }

    @PostMapping("/api/auth/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/api/auth/logout")
    public ApiResponse<Map<String, Object>> logout(@AuthenticationPrincipal LoginUser loginUser) {
        authService.logout(loginUser);
        return ApiResponse.ok();
    }

    @PostMapping("/api/auth/dev-login")
    public ApiResponse<AuthTokenResponse> devLogin(@RequestBody DevLoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.ok(authService.devLogin(request.devOpenid(), httpRequest.getServerName(), ClientIp.resolve(httpRequest)));
    }
}
