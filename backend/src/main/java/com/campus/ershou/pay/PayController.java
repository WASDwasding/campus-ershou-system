package com.campus.ershou.pay;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.web.ClientIp;
import com.campus.ershou.pay.dto.MockConfirmRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PayController {

    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    @GetMapping("/api/payments/{paymentNo}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable String paymentNo,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(payService.detail(loginUser.userId(), paymentNo, ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/pay/mock/{paymentNo}/confirm")
    public ApiResponse<Map<String, Object>> confirm(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable String paymentNo,
            @RequestBody MockConfirmRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(payService.confirm(
                loginUser.userId(), paymentNo, request.result(), ClientIp.resolve(httpRequest)));
    }

    @PostMapping("/api/pay/notify/mock")
    public ResponseEntity<Map<String, String>> notify(@RequestBody Map<String, Object> body, HttpServletRequest httpRequest) {
        try {
            payService.handleNotify(stringify(body), ClientIp.resolve(httpRequest));
            return ResponseEntity.ok(Map.of("code", "SUCCESS", "message", "OK"));
        } catch (PayNotifyException ex) {
            return ResponseEntity.badRequest().body(Map.of("code", "FAIL", "message", ex.getMessage()));
        }
    }

    private static Map<String, String> stringify(Map<String, Object> body) {
        Map<String, String> values = new java.util.LinkedHashMap<>();
        if (body == null) {
            return values;
        }
        body.forEach((key, value) -> {
            if (value != null) {
                values.put(key, String.valueOf(value));
            }
        });
        return values;
    }
}
