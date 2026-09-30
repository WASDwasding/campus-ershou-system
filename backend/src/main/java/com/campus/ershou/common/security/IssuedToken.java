package com.campus.ershou.common.security;

import java.time.OffsetDateTime;

public record IssuedToken(String token, String jti, OffsetDateTime expiresAt) {
}
