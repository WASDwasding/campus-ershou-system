package com.campus.ershou.user.controller;

import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.web.NotImplemented;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Void>> me() {
        return NotImplemented.of("当前用户资料尚未实现");
    }
}
