package com.campus.ershou.order.dto;

import java.util.List;

public record DisputeRequest(String reason, String description, List<String> evidenceFileIds) {
}
