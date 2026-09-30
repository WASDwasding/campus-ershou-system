package com.campus.ershou.common.web;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIp {

    private ClientIp() {
    }

    public static String resolve(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank() && realIp.indexOf(',') < 0) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
