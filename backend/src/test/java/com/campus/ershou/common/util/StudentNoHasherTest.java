package com.campus.ershou.common.util;

import com.campus.ershou.common.config.AppProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentNoHasherTest {

    @Test
    void hashIsStableAndMaskKeepsHeadAndTail() {
        AppProperties props = new AppProperties();
        props.getStudentNo().setHmacKey("unit-test-hmac-key-16");
        StudentNoHasher hasher = new StudentNoHasher(props);

        assertThat(hasher.hash("2023123456")).isEqualTo(hasher.hash("2023123456"));
        assertThat(hasher.hash("2023123456")).isNotEqualTo(hasher.hash("2023123457"));
        assertThat(hasher.hash("2023123456")).hasSize(64);
        assertThat(hasher.mask("2023123456")).isEqualTo("2023****56");
    }
}
