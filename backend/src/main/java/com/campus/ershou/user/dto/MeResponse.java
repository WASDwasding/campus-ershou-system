package com.campus.ershou.user.dto;

import java.time.OffsetDateTime;

public record MeResponse(
        long id,
        String username,
        String nickname,
        String emailMasked,
        String studentNoMasked,
        String avatarUrl,
        String bio,
        String status,
        String verifyStatus,
        DormBuildingView dormBuilding,
        OffsetDateTime createdAt
) {
    public record DormBuildingView(long id, String name) {
    }
}
