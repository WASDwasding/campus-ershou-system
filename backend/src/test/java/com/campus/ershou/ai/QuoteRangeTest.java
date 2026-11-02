package com.campus.ershou.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteRangeTest {

    @Test
    void usesIntegerPercentOfListPrice() {
        QuoteRange range = QuoteRange.of(1500, "GOOD");

        assertThat(range.floor()).isEqualTo(1050);
        assertThat(range.ceiling()).isEqualTo(1500);
        assertThat(range.rulePrice()).isEqualTo(1275);
        assertThat(range.contains(1200)).isTrue();
        assertThat(range.contains(100)).isFalse();
    }

    @Test
    void keepsFloorInsideCeiling() {
        QuoteRange poor = QuoteRange.of(1, "POOR");
        QuoteRange fresh = QuoteRange.of(10, "NEW");

        assertThat(poor.floor()).isEqualTo(1);
        assertThat(poor.ceiling()).isEqualTo(1);
        assertThat(poor.rulePrice()).isEqualTo(1);
        assertThat(fresh.floor()).isEqualTo(9);
        assertThat(fresh.rulePrice()).isEqualTo(9);
    }
}
