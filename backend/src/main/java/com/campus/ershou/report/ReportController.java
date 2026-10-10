package com.campus.ershou.report;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.web.ClientIp;
import com.campus.ershou.report.dto.CreateReportRequest;
import com.campus.ershou.report.dto.HandleReportRequest;
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
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/api/reports")
    public ApiResponse<Map<String, Object>> create(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody CreateReportRequest request
    ) {
        return ApiResponse.ok(reportService.create(
                loginUser.userId(), request.targetType(), request.targetId(), request.reason(),
                request.description(), request.evidenceFileIds()));
    }

    @GetMapping("/api/me/reports")
    public ApiResponse<Map<String, Object>> mine(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(reportService.mine(loginUser.userId(), page, size));
    }

    @GetMapping("/api/admin/reports")
    public ApiResponse<Map<String, Object>> adminList(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(reportService.adminList(status, targetType, page, size));
    }

    @GetMapping("/api/admin/reports/{id}")
    public ApiResponse<Map<String, Object>> adminDetail(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id
    ) {
        return ApiResponse.ok(reportService.adminDetail(id));
    }

    @PostMapping("/api/admin/reports/{id}/handle")
    public ApiResponse<Map<String, Object>> handle(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @RequestBody HandleReportRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(reportService.handle(
                loginAdmin.adminId(), id, request.action(), request.banDays(), request.reason(), ClientIp.resolve(httpRequest)));
    }
}
