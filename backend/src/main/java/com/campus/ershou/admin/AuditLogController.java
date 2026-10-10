package com.campus.ershou.admin;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuditLogController {

    private final AuditQueryService auditQueryService;

    public AuditLogController(AuditQueryService auditQueryService) {
        this.auditQueryService = auditQueryService;
    }

    @GetMapping("/api/admin/audit-logs")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String event,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(auditQueryService.list(category, event, userId, ip, from, to, page, size));
    }
}
