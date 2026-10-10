package com.campus.ershou.admin;

import com.campus.ershou.auth.AdminVerificationService;
import com.campus.ershou.order.OrderService;
import com.campus.ershou.report.ReportService;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AdminDashboardController {

    private final AdminVerificationService adminVerificationService;
    private final ReportService reportService;
    private final OrderService orderService;

    public AdminDashboardController(
            AdminVerificationService adminVerificationService,
            ReportService reportService,
            OrderService orderService
    ) {
        this.adminVerificationService = adminVerificationService;
        this.reportService = reportService;
        this.orderService = orderService;
    }

    @GetMapping("/api/admin/dashboard")
    public ApiResponse<Map<String, Object>> dashboard(@AuthenticationPrincipal LoginAdmin loginAdmin) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pendingVerifications", adminVerificationService.countPending());
        data.put("pendingReports", reportService.countPending());
        data.put("openDisputes", orderService.countOpenDisputes());
        return ApiResponse.ok(data);
    }
}
