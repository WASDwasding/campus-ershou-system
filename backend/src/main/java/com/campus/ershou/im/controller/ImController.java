package com.campus.ershou.im.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/im")
public class ImController {

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<Void>> history() {
        return NotImplemented.of("聊天记录尚未实现");
    }
}
