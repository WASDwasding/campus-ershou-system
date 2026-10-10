package com.campus.ershou.goods;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.web.ClientIp;
import com.campus.ershou.goods.dto.RemoveGoodsRequest;
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
public class GoodsAdminController {

    private final GoodsAdminService goodsAdminService;

    public GoodsAdminController(GoodsAdminService goodsAdminService) {
        this.goodsAdminService = goodsAdminService;
    }

    @GetMapping("/api/admin/goods")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(goodsAdminService.list(status, keyword, sellerId, page, size));
    }

    @PostMapping("/api/admin/goods/{goodsId}/remove")
    public ApiResponse<Map<String, Object>> remove(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long goodsId,
            @RequestBody RemoveGoodsRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(goodsAdminService.remove(
                loginAdmin.adminId(), goodsId, request.reason(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/admin/goods/{goodsId}/restore")
    public ApiResponse<Map<String, Object>> restore(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long goodsId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(goodsAdminService.restore(loginAdmin.adminId(), goodsId, ClientIp.resolve(httpRequest)));
    }
}
