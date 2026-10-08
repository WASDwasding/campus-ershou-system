package com.campus.ershou.common.security;

import java.time.OffsetDateTime;

public record LoginAdmin(long adminId, int tokenVersion, String jti, OffsetDateTime expiresAt) {
}
