package com.campus.ershou.auth.dto;

public record SetupTokenResponse(String setupToken, int expiresInSeconds) {
}
