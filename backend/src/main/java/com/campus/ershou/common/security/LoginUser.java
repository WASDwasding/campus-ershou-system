package com.campus.ershou.common.security;

import java.time.OffsetDateTime;

public record LoginUser(long userId, int tokenVersion, String jti, OffsetDateTime expiresAt) {
}
