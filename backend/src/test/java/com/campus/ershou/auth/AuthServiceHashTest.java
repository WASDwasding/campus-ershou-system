package com.campus.ershou.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceHashTest {

    @Test
    void sha256IsDeterministicAndDifferentForDifferentCodes() {
        assertThat(AuthService.sha256("123456")).isEqualTo(AuthService.sha256("123456"));
        assertThat(AuthService.sha256("123456")).isNotEqualTo(AuthService.sha256("123457"));
        assertThat(AuthService.sha256("123456")).hasSize(64);
    }
}
