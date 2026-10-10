package com.campus.ershou.im;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.im.dto.ReadNotificationsRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/api/notifications")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(notificationService.list(loginUser.userId(), unreadOnly, page, size));
    }

    @PostMapping("/api/notifications/read")
    public ApiResponse<Map<String, Object>> read(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody ReadNotificationsRequest request
    ) {
        return ApiResponse.ok(notificationService.markRead(loginUser.userId(), request));
    }
}
