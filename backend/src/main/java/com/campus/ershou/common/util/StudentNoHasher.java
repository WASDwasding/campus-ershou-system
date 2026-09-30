package com.campus.ershou.common.util;

import com.campus.ershou.common.config.AppProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

@Component
public class StudentNoHasher {

    private final byte[] key;

    public StudentNoHasher(AppProperties props) {
        this.key = props.getStudentNo().getHmacKey().getBytes(StandardCharsets.UTF_8);
    }

    public String hash(String studentNo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(studentNo.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("无法计算学号摘要", e);
        }
    }

    public String mask(String studentNo) {
        if (studentNo == null || studentNo.length() < 6) {
            return "****";
        }
        return studentNo.substring(0, 4) + "****" + studentNo.substring(studentNo.length() - 2);
    }
}
