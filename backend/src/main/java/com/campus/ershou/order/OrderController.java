package com.campus.ershou.order;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginAdmin;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.web.ClientIp;
import com.campus.ershou.order.dto.CancelOrderRequest;
import com.campus.ershou.order.dto.CreateOrderRequest;
import com.campus.ershou.order.dto.DisputeRequest;
import com.campus.ershou.order.dto.MeetupRequest;
import com.campus.ershou.order.dto.ResolveDisputeRequest;
import com.campus.ershou.order.dto.ReviewRequest;
import com.campus.ershou.pay.PayService;
import com.campus.ershou.pay.dto.CreatePaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final PayService payService;

    public OrderController(OrderService orderService, PayService payService) {
        this.orderService = orderService;
        this.payService = payService;
    }

    @PostMapping("/api/orders")
    public ApiResponse<Map<String, Object>> create(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.create(
                loginUser.userId(), request.goodsId(), request.quantity(), idempotencyKey, ClientIp.resolve(httpRequest)));
    }

    @GetMapping("/api/orders/{orderId}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.detail(loginUser.userId(), orderId, ClientIp.resolve(httpRequest)));
    }

    @GetMapping("/api/me/orders")
    public ApiResponse<Map<String, Object>> mine(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(orderService.listMine(loginUser.userId(), role, status, page, size));
    }

    @PostMapping("/api/orders/{orderId}/cancel")
    public ApiResponse<Map<String, Object>> cancel(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            @RequestBody(required = false) CancelOrderRequest request,
            HttpServletRequest httpRequest
    ) {
        String reason = request == null ? null : request.reason();
        return ApiResponse.ok(orderService.cancel(loginUser.userId(), orderId, reason, ClientIp.resolve(httpRequest)));
    }

    @PutMapping("/api/orders/{orderId}/meetup")
    public ApiResponse<Map<String, Object>> meetup(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            @RequestBody MeetupRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.meetup(
                loginUser.userId(), orderId, request.buildingId(), request.place(), request.time(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/orders/{orderId}/handover-confirm")
    public ApiResponse<Map<String, Object>> handover(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.handover(loginUser.userId(), orderId, ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/orders/{orderId}/dispute")
    public ApiResponse<Map<String, Object>> dispute(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            @RequestBody DisputeRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.dispute(
                loginUser.userId(), orderId, request.reason(), request.description(), request.evidenceFileIds(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/orders/{orderId}/dispute/withdraw")
    public ApiResponse<Map<String, Object>> withdraw(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.withdrawDispute(loginUser.userId(), orderId, ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/orders/{orderId}/review")
    public ApiResponse<Map<String, Object>> review(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            @RequestBody ReviewRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.review(
                loginUser.userId(), orderId, request.score(), request.content(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/orders/{orderId}/payments")
    public ApiResponse<Map<String, Object>> pay(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long orderId,
            @RequestBody CreatePaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(payService.create(
                loginUser.userId(), orderId, request.channel(), idempotencyKey, ClientIp.resolve(httpRequest)));
    }

    @GetMapping("/api/users/{userId}/reviews")
    public ApiResponse<Map<String, Object>> reviews(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(orderService.reviewsOf(userId, page, size));
    }

    @GetMapping("/api/admin/orders")
    public ApiResponse<Map<String, Object>> adminList(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(orderService.adminList(status, orderNo, buyerId, sellerId, page, size));
    }

    @GetMapping("/api/admin/orders/{orderId}")
    public ApiResponse<Map<String, Object>> adminDetail(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long orderId
    ) {
        return ApiResponse.ok(orderService.adminDetail(orderId));
    }

    @GetMapping("/api/admin/disputes")
    public ApiResponse<Map<String, Object>> disputes(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(orderService.adminDisputes(status, page, size));
    }

    @PostMapping("/api/admin/disputes/{id}/resolve")
    public ApiResponse<Map<String, Object>> resolve(
            @AuthenticationPrincipal LoginAdmin loginAdmin,
            @PathVariable long id,
            @RequestBody ResolveDisputeRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(orderService.resolve(
                loginAdmin.adminId(), id, request.result(), request.remark(), ClientIp.resolve(httpRequest)));
    }
}
