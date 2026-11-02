package com.campus.ershou.ai;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class ModelJson {

    private ModelJson() {
    }

    static JsonNode object(ObjectMapper mapper, String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = stripFence(raw.trim());
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        try {
            JsonNode node = mapper.readTree(trimmed.substring(start, end + 1));
            return node != null && node.isObject() ? node : null;
        } catch (JacksonException ex) {
            return null;
        }
    }

    private static String stripFence(String raw) {
        if (!raw.startsWith("```")) {
            return raw;
        }
        int newline = raw.indexOf('\n');
        int close = raw.lastIndexOf("```");
        if (newline >= 0 && close > newline) {
            return raw.substring(newline + 1, close).trim();
        }
        return raw;
    }
}
