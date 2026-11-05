package com.campus.ershou.report.dto;

import java.util.List;

public record CreateReportRequest(Long goodsId, String description, List<String> evidenceFileIds) {
}
