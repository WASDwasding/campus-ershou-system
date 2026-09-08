package com.campus.ershou.pay.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    @PostMapping("/mock")
    public ResponseEntity<ApiResponse<Void>> mockPay() {
        return NotImplemented.of("模拟支付尚未实现：禁止客户端传 paid=true，只允许 PENDING_PAY -> PAID");
    }
}
