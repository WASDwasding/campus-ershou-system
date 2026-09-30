package com.campus.ershou.common.campus;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.api.FieldErrorItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class CampusController {

    private static final Set<String> BUILDING_TYPES = Set.of(
            "DORM", "TEACHING", "CANTEEN", "LIBRARY", "GATE", "OTHER"
    );

    private final BuildingMapper buildingMapper;
    private final CategoryMapper categoryMapper;

    public CampusController(BuildingMapper buildingMapper, CategoryMapper categoryMapper) {
        this.buildingMapper = buildingMapper;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/api/categories")
    public ApiResponse<Map<String, Object>> categories() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", categoryMapper.listEnabled());
        return ApiResponse.ok(data);
    }

    @GetMapping("/api/campus/buildings")
    public ApiResponse<Map<String, Object>> buildings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "false") boolean meetupOnly
    ) {
        String normalized = type == null || type.isBlank() ? null : type.trim().toUpperCase();
        if (normalized != null && !BUILDING_TYPES.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("type", "楼栋类型不合法")));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", buildingMapper.listEnabled(normalized, meetupOnly));
        return ApiResponse.ok(data);
    }
}
