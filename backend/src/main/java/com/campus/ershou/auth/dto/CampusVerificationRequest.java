package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CampusVerificationRequest(
        @NotNull(message = "宿舍楼不能为空")
        Long dormBuildingId,

        @NotBlank(message = "证件照不能为空")
        String cardImageFileId
) {
    public CampusVerificationRequest {
        cardImageFileId = cardImageFileId == null ? null : cardImageFileId.trim();
    }
}
