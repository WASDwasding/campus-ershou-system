package com.campus.ershou.order.dto;

import java.time.OffsetDateTime;

public record MeetupRequest(Long buildingId, String place, OffsetDateTime time) {
}
