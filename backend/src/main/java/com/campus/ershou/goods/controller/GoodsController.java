package com.campus.ershou.goods.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @GetMapping
    public ResponseEntity<ApiResponse<Void>> list() {
        return NotImplemented.of("商品列表尚未实现");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> detail(@PathVariable Long id) {
        return NotImplemented.of("商品详情尚未实现");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> publish() {
        return NotImplemented.of("发布商品尚未实现");
    }
}
