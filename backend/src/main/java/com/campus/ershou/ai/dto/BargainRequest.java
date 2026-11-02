package com.campus.ershou.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BargainRequest(
        @NotNull(message = "商品不能为空") Long goodsId,
        @NotBlank(message = "报价令牌不能为空") String quoteToken,
        @Size(max = 200, message = "议价说明最多 200 字") String message
) {
    public BargainRequest {
        if (quoteToken != null) {
            quoteToken = quoteToken.trim();
        }
        if (message != null) {
            message = message.trim();
            if (message.isEmpty()) {
                message = null;
            }
        }
    }
}
