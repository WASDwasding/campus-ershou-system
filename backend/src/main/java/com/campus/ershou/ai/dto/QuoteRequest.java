package com.campus.ershou.ai.dto;

import jakarta.validation.constraints.NotNull;

public record QuoteRequest(
        @NotNull(message = "商品不能为空") Long goodsId
) {
}
