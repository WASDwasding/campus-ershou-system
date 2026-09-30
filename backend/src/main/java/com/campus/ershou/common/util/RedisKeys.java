package com.campus.ershou.common.util;

public final class RedisKeys {

    public static final String PREFIX = "campus:";

    private RedisKeys() {
    }

    public static String pendingRegister(String email) {
        return PREFIX + "reg:pending:" + email;
    }

    public static String setupToken(String token) {
        return PREFIX + "reg:setup:" + token;
    }

    public static String jwtDeny(String jti) {
        return PREFIX + "jwt:deny:" + jti;
    }

    public static String tokenVer(long userId) {
        return PREFIX + "user:tokenver:" + userId;
    }

    public static String rateLimit(String rule, String id, String window) {
        return PREFIX + "rl:" + rule + ":" + id + ":" + window;
    }

    public static String sendCooldown(String email) {
        return PREFIX + "rl:send-code:" + email;
    }

    public static String loginFail(String studentNoHash) {
        return PREFIX + "rl:login-fail:" + studentNoHash;
    }

    public static String loginLock(String studentNoHash) {
        return PREFIX + "rl:login-lock:" + studentNoHash;
    }
}
