package com.campus.ershou.common.web;

import com.campus.ershou.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of(
                "service", "campus-ershou",
                "status", "UP",
                "time", Instant.now().toString()
        ));
    }
}
