package com.campus.ershou.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ContactInfo {

    private static final Pattern MOBILE = Pattern.compile("(?<!\\d)1[3-9]\\d(?:[\\s-]?\\d){8}(?!\\d)");
    private static final Pattern WECHAT = Pattern.compile(
            "(?:微信|威信|薇信|v信|vx|wx|weixin|wechat|加v|\\+v)[\\s:：]*[a-z][-_a-z0-9]{5,19}",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern QQ = Pattern.compile("(?:qq|扣扣|企鹅)[\\s:：]*[1-9]\\d{4,10}", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}", Pattern.CASE_INSENSITIVE);

    private ContactInfo() {
    }

    public static boolean contains(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        return MOBILE.matcher(normalized).find()
                || WECHAT.matcher(normalized).find()
                || QQ.matcher(normalized).find()
                || EMAIL.matcher(normalized).find();
    }

    public static String mask(String text) {
        if (text == null) {
            return null;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);
        String masked = MOBILE.matcher(normalized).replaceAll("[联系方式已隐藏]");
        masked = WECHAT.matcher(masked).replaceAll("[联系方式已隐藏]");
        masked = QQ.matcher(masked).replaceAll("[联系方式已隐藏]");
        masked = EMAIL.matcher(masked).replaceAll("[联系方式已隐藏]");
        return masked;
    }
}
