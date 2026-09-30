package com.campus.ershou.common.meta;

import com.campus.ershou.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping("/api/meta")
    public ApiResponse<Map<String, Object>> meta() {
        return ApiResponse.ok(metaService.meta());
    }
}
