package com.campus.ershou.ai;

public record AiPart(String text, byte[] jpeg) {

    public static AiPart text(String text) {
        return new AiPart(text, null);
    }

    public static AiPart jpeg(byte[] jpeg) {
        return new AiPart(null, jpeg);
    }
}
