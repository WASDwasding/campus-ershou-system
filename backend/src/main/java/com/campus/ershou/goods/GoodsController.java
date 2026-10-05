package com.campus.ershou.goods;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.goods.dto.GoodsWriteRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("/api/goods")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Long meetupBuildingId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int[] paging = GoodsService.page(page, size);
        GoodsListQuery query = new GoodsListQuery();
        query.setKeyword(keyword);
        query.setCategoryId(categoryId);
        query.setCondition(condition);
        query.setMinPrice(minPrice);
        query.setMaxPrice(maxPrice);
        query.setMeetupBuildingId(meetupBuildingId);
        query.setAreaId(areaId);
        query.setSort(sort);
        query.setLimit(paging[1]);
        query.setOffset((paging[0] - 1) * paging[1]);
        return ApiResponse.ok(goodsService.list(loginUser.userId(), query));
    }

    @GetMapping("/api/goods/{goodsId}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.detail(loginUser.userId(), goodsId));
    }

    @PostMapping("/api/goods")
    public ApiResponse<Map<String, Object>> create(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody GoodsWriteRequest request
    ) {
        return ApiResponse.ok(goodsService.create(loginUser.userId(), request));
    }

    @PutMapping("/api/goods/{goodsId}")
    public ApiResponse<Map<String, Object>> update(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId,
            @Valid @RequestBody GoodsWriteRequest request
    ) {
        return ApiResponse.ok(goodsService.update(loginUser.userId(), goodsId, request));
    }

    @PostMapping("/api/goods/{goodsId}/off-shelf")
    public ApiResponse<Map<String, Object>> offShelf(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.changeShelf(loginUser.userId(), goodsId, false));
    }

    @PostMapping("/api/goods/{goodsId}/on-shelf")
    public ApiResponse<Map<String, Object>> onShelf(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.changeShelf(loginUser.userId(), goodsId, true));
    }

    @DeleteMapping("/api/goods/{goodsId}")
    public ApiResponse<Map<String, Object>> delete(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.delete(loginUser.userId(), goodsId));
    }

    @GetMapping("/api/me/goods")
    public ApiResponse<Map<String, Object>> mine(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int[] paging = GoodsService.page(page, size);
        return ApiResponse.ok(goodsService.mine(loginUser.userId(), status, paging[0], paging[1]));
    }

    @GetMapping("/api/users/{userId}/goods")
    public ApiResponse<Map<String, Object>> bySeller(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int[] paging = GoodsService.page(page, size);
        return ApiResponse.ok(goodsService.bySeller(loginUser.userId(), userId, status, paging[0], paging[1]));
    }

    @PutMapping("/api/goods/{goodsId}/favorite")
    public ApiResponse<Map<String, Object>> favorite(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.favorite(loginUser.userId(), goodsId, true));
    }

    @DeleteMapping("/api/goods/{goodsId}/favorite")
    public ApiResponse<Map<String, Object>> unfavorite(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long goodsId
    ) {
        return ApiResponse.ok(goodsService.favorite(loginUser.userId(), goodsId, false));
    }

    @GetMapping("/api/me/favorites")
    public ApiResponse<Map<String, Object>> favorites(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int[] paging = GoodsService.page(page, size);
        return ApiResponse.ok(goodsService.favorites(loginUser.userId(), paging[0], paging[1]));
    }

    @GetMapping("/api/me/history")
    public ApiResponse<Map<String, Object>> history(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int[] paging = GoodsService.page(page, size);
        return ApiResponse.ok(goodsService.history(loginUser.userId(), paging[0], paging[1]));
    }

    @DeleteMapping("/api/me/history")
    public ApiResponse<Map<String, Object>> clearHistory(@AuthenticationPrincipal LoginUser loginUser) {
        goodsService.clearHistory(loginUser.userId());
        return ApiResponse.ok();
    }
}
