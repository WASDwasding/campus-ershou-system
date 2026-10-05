package com.campus.ershou.user;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.user.dto.MeResponse;
import com.campus.ershou.user.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;

    public UserController(UserService userService, UserQueryService userQueryService) {
        this.userService = userService;
        this.userQueryService = userQueryService;
    }

    @GetMapping("/api/me")
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(userService.getMe(loginUser.userId()));
    }

    @PutMapping("/api/me/profile")
    public ApiResponse<MeResponse> updateProfile(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ApiResponse.ok(userService.updateProfile(loginUser.userId(), request));
    }

    @GetMapping("/api/me/stats")
    public ApiResponse<java.util.Map<String, Object>> stats(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(userQueryService.stats(loginUser.userId()));
    }

    @GetMapping("/api/me/badges")
    public ApiResponse<java.util.Map<String, Object>> badges(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.ok(userQueryService.badges(loginUser.userId()));
    }

    @GetMapping("/api/users/{userId}")
    public ApiResponse<java.util.Map<String, Object>> publicProfile(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long userId
    ) {
        return ApiResponse.ok(userQueryService.publicProfile(loginUser.userId(), userId));
    }
}
