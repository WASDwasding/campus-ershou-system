package com.campus.ershou.common.meta;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MetaServiceTest {

    @Test
    void containsCoreEnumsAndLimits() {
        Map<String, Object> meta = new MetaService().meta();
        @SuppressWarnings("unchecked")
        Map<String, Object> enums = (Map<String, Object>) meta.get("enums");
        @SuppressWarnings("unchecked")
        Map<String, Object> limits = (Map<String, Object>) meta.get("limits");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> conditions = (List<Map<String, String>>) enums.get("goodsCondition");

        assertThat(conditions).extracting(item -> item.get("value")).contains("NEW", "POOR");
        assertThat(limits).containsEntry("priceMin", 1).containsEntry("passwordMin", 8);
    }
}
