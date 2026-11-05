package com.campus.ershou.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CampusVerificationRequest(
        @NotBlank(message = "姓名不能为空")
        @Size(min = 1, max = 20, message = "姓名长度为 1–20")
        String realName,

        @NotBlank(message = "学号不能为空")
        @Pattern(regexp = "\\d{8,12}", message = "学号为 8–12 位数字")
        String studentNo,

        @NotBlank(message = "学院不能为空")
        @Size(min = 1, max = 30, message = "学院长度为 1–30")
        String college,

        @NotNull(message = "宿舍楼不能为空")
        Long dormBuildingId,

        @NotBlank(message = "证件照不能为空")
        String cardImageFileId
) {
    public CampusVerificationRequest {
        realName = realName == null ? null : realName.trim();
        studentNo = studentNo == null ? null : studentNo.trim();
        college = college == null ? null : college.trim();
        cardImageFileId = cardImageFileId == null ? null : cardImageFileId.trim();
    }
}
