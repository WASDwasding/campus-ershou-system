package com.campus.ershou.common.security;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.util.Randoms;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final AppProperties props;
    private final SecretKey key;

    public JwtService(AppProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public IssuedToken issueUser(long userId, int tokenVersion) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getJwt().getUserTtl());
        String jti = Randoms.hex(16);
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "user")
                .claim("ver", tokenVersion)
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
        return new IssuedToken(token, jti, OffsetDateTime.ofInstant(exp, ZoneOffset.ofHours(8)));
    }

    public LoginUser parseUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!"user".equals(claims.get("typ", String.class))) {
                throw ApiException.of(ErrorCode.UNAUTHORIZED);
            }
            long userId = Long.parseLong(claims.getSubject());
            Number ver = claims.get("ver", Number.class);
            if (ver == null || claims.getId() == null || claims.getExpiration() == null) {
                throw ApiException.of(ErrorCode.UNAUTHORIZED);
            }
            OffsetDateTime expiresAt = OffsetDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneOffset.ofHours(8));
            return new LoginUser(userId, ver.intValue(), claims.getId(), expiresAt);
        } catch (ExpiredJwtException ex) {
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("TOKEN_INVALID");
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        }
    }
}
