package com.campus.ershou.im.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record ReadNotificationsRequest(
        @Size(max = 100, message = "一次最多标记 100 条")
        List<Long> ids,
        Boolean all
) {
}
