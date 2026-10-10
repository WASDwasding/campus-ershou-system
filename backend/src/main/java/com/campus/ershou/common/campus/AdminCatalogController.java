package com.campus.ershou.common.campus;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;

    public AdminCatalogController(AdminCatalogService adminCatalogService) {
        this.adminCatalogService = adminCatalogService;
    }

    @GetMapping("/api/admin/categories")
    public ApiResponse<Map<String, Object>> categories(@AuthenticationPrincipal LoginAdmin loginAdmin) {
        return ApiResponse.ok(adminCatalogService.categories());
    }

    @PostMapping("/api/admin/categories")
    public ApiResponse<Map<String, Object>> createCategory(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestBody CatalogWriteRequest request
    ) {
        return ApiResponse.ok(adminCatalogService.createCategory(request.name(), request.icon(), request.sort(), request.enabled()));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ApiResponse<Map<String, Object>> updateCategory(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @RequestBody CatalogWriteRequest request
    ) {
        return ApiResponse.ok(adminCatalogService.updateCategory(id, request.name(), request.icon(), request.sort(), request.enabled()));
    }

    @GetMapping("/api/admin/buildings")
    public ApiResponse<Map<String, Object>> buildings(@AuthenticationPrincipal LoginAdmin loginAdmin) {
        return ApiResponse.ok(adminCatalogService.buildings());
    }

    @PostMapping("/api/admin/buildings")
    public ApiResponse<Map<String, Object>> createBuilding(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestBody BuildingWriteRequest request
    ) {
        return ApiResponse.ok(adminCatalogService.createBuilding(
                request.name(), request.areaId(), request.type(), request.meetupPoint(), request.sort(), request.enabled()));
    }

    @PutMapping("/api/admin/buildings/{id}")
    public ApiResponse<Map<String, Object>> updateBuilding(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @RequestBody BuildingWriteRequest request
    ) {
        return ApiResponse.ok(adminCatalogService.updateBuilding(
                id, request.name(), request.areaId(), request.type(), request.meetupPoint(), request.sort(), request.enabled()));
    }

    public record CatalogWriteRequest(String name, String icon, Integer sort, Boolean enabled) {
    }

    public record BuildingWriteRequest(String name, Long areaId, String type, Boolean meetupPoint, Integer sort, Boolean enabled) {
    }
}
