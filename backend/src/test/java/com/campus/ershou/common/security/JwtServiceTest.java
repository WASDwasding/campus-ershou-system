package com.campus.ershou.common.security;

import com.campus.ershou.common.config.AppProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void issueAndParseRoundTrip() {
        AppProperties props = new AppProperties();
        props.getJwt().setSecret("unit-test-jwt-secret-key-32bytes!");
        JwtService jwtService = new JwtService(props);

        IssuedToken issued = jwtService.issueUser(42L, 3);
        LoginUser parsed = jwtService.parseUser(issued.token());

        assertThat(parsed.userId()).isEqualTo(42L);
        assertThat(parsed.tokenVersion()).isEqualTo(3);
        assertThat(parsed.jti()).isEqualTo(issued.jti());
        assertThat(parsed.expiresAt().toEpochSecond()).isEqualTo(issued.expiresAt().toEpochSecond());
    }
}
