package com.campus.ershou.order.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create() {
        return NotImplemented.of("下单尚未实现：金额必须服务端计算，库存用 GoodsMapper.deductStock");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Void>> list() {
        return NotImplemented.of("订单列表尚未实现");
    }
}
