package com.campus.ershou.report.dto;

import java.util.List;

public record CreateReportRequest(
        String targetType,
        Long targetId,
        String reason,
        String description,
        List<String> evidenceFileIds
) {
}
