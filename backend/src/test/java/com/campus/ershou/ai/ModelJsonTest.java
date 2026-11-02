package com.campus.ershou.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readsObjectInsideFence() {
        JsonNode node = ModelJson.object(mapper, """
                好的
                ```json
                {"title":"耳机"}
                ```
                """);

        assertThat(node).isNotNull();
        assertThat(node.get("title").asText()).isEqualTo("耳机");
    }

    @Test
    void rejectsNonObject() {
        assertThat(ModelJson.object(mapper, "不是 JSON")).isNull();
        assertThat(ModelJson.object(mapper, "[1,2]")).isNull();
    }
}
