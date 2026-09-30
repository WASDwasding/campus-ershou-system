package com.campus.ershou.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegisterSentResponse(String emailMasked, int expiresInSeconds, String debugCode) {
}
