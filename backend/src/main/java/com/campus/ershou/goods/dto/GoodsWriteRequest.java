package com.campus.ershou.goods.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GoodsWriteRequest(
        @NotBlank(message = "标题不能为空")
        @Size(min = 2, max = 30, message = "标题长度为 2–30")
        String title,

        @Size(max = 500, message = "描述最多 500 字")
        String description,

        @NotNull(message = "分类不能为空")
        Long categoryId,

        @NotBlank(message = "成色不能为空")
        String condition,

        @NotNull(message = "价格不能为空")
        @Min(value = 1, message = "价格至少 1 分")
        @Max(value = 9999900, message = "价格过高")
        Integer price,

        @Min(value = 1, message = "原价至少 1 分")
        @Max(value = 99999900, message = "原价过高")
        Integer originalPrice,

        @NotNull(message = "商品图片不能为空")
        @Size(min = 1, max = 9, message = "商品图片为 1–9 张")
        List<String> imageFileIds,

        @NotNull(message = "面交楼栋不能为空")
        Long meetupBuildingId,

        @Size(max = 100, message = "交易说明最多 100 字")
        String tradeNote
) {
    public GoodsWriteRequest {
        title = title == null ? null : title.trim();
        description = blankToNull(description);
        condition = condition == null ? null : condition.trim().toUpperCase();
        tradeNote = blankToNull(tradeNote);
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
