package com.campus.ershou.common.util;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class Randoms {

    private static final SecureRandom RANDOM = new SecureRandom();

    private Randoms() {
    }

    public static String digits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String hex(int bytes) {
        byte[] buf = new byte[bytes];
        RANDOM.nextBytes(buf);
        return HexFormat.of().formatHex(buf);
    }
}
