package com.campus.ershou.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GoodsDraftRequest(
        @NotNull(message = "商品图片不能为空")
        @Size(min = 1, max = 9, message = "商品图片为 1–9 张")
        List<String> imageFileIds,

        @Size(max = 200, message = "补充说明最多 200 字")
        String prompt
) {
    public GoodsDraftRequest {
        if (prompt != null) {
            prompt = prompt.trim();
            if (prompt.isEmpty()) {
                prompt = null;
            }
        }
    }
}
