package com.campus.ershou.user.dto;

import java.time.OffsetDateTime;

public record MeResponse(
        long id,
        String username,
        String nickname,
        String avatarUrl,
        String bio,
        String status,
        String verifyStatus,
        String realName,
        String college,
        String studentNoMasked,
        String emailMasked,
        boolean wxBound,
        DormBuildingView dormBuilding,
        boolean publishBanned,
        OffsetDateTime createdAt
) {
    public record DormBuildingView(long id, String name) {
    }
}
