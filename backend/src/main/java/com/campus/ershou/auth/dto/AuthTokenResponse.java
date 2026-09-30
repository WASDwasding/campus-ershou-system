package com.campus.ershou.auth.dto;

import com.campus.ershou.user.dto.MeResponse;

import java.time.OffsetDateTime;

public record AuthTokenResponse(
        String token,
        OffsetDateTime expiresAt,
        boolean isNewUser,
        MeResponse user
) {
}
