package com.campus.ershou.common.file;

import com.campus.ershou.common.config.AppProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class FileUrls {

    private final AppProperties props;

    public FileUrls(AppProperties props) {
        this.props = props;
    }

    public String publicUrl(String fileId) {
        return base() + "/files/public/" + fileId;
    }

    public String thumbUrl(String fileId) {
        return publicUrl(fileId) + "?size=thumb";
    }

    public String privateUrl(String fileId, String size, long exp) {
        String normalized = normalizeSize(size);
        String sig = sign(fileId, normalized, exp);
        String url = base() + "/api/files/private/" + fileId + "?exp=" + exp + "&sig=" + sig;
        if (!normalized.isEmpty()) {
            url += "&size=" + normalized;
        }
        return url;
    }

    public boolean validSignature(String fileId, String size, long exp, String sig) {
        if (sig == null || sig.isBlank() || Instant.now().getEpochSecond() > exp) {
            return false;
        }
        byte[] expected = sign(fileId, normalizeSize(size), exp).getBytes(StandardCharsets.UTF_8);
        byte[] actual = sig.trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }

    public long privateExpiryEpoch() {
        return Instant.now().plus(props.getFile().getPrivateUrlTtl()).getEpochSecond();
    }

    public String normalizeSize(String size) {
        if (size == null || size.isBlank()) {
            return "";
        }
        if ("thumb".equals(size)) {
            return "thumb";
        }
        throw new IllegalArgumentException("size");
    }

    private String sign(String fileId, String size, long exp) {
        String payload = fileId + "|" + size + "|" + exp;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(props.getFile().getSignKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("无法签名文件地址", e);
        }
    }

    private String base() {
        String base = props.getFile().getPublicBaseUrl();
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }
}
