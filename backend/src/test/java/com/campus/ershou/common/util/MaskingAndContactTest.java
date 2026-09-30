package com.campus.ershou.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingAndContactTest {

    @Test
    void emailKeepsFirstCharAndDomain() {
        assertThat(Masking.email("alice@school.edu.cn")).isEqualTo("a***@school.edu.cn");
    }

    @Test
    void contactInfoDetectsPhoneWechatAndEmail() {
        assertThat(ContactInfo.contains("13812345678")).isTrue();
        assertThat(ContactInfo.contains("微信: abcdefg")).isTrue();
        assertThat(ContactInfo.contains("user@example.com")).isTrue();
        assertThat(ContactInfo.contains("校园二手小明")).isFalse();
    }
}
