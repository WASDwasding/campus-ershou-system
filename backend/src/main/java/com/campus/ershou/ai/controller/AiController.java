package com.campus.ershou.ai.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @PostMapping("/price-suggest")
    public ResponseEntity<ApiResponse<Void>> priceSuggest() {
        return NotImplemented.of("智能定价尚未实现");
    }

    @PostMapping("/bargain")
    public ResponseEntity<ApiResponse<Void>> bargain() {
        return NotImplemented.of("议价客服尚未实现");
    }
}
