package com.campus.ershou.pay;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PaySignerTest {

    @Test
    void matchesTheSpecExample() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("amount", "1500");
        fields.put("gatewayTradeNo", "MOCK20260929101602483911");
        fields.put("nonce", "9f2c7a1e4b6d8f03");
        fields.put("orderNo", "O202609291015004821");
        fields.put("paymentNo", "P202609291015304107");
        fields.put("status", "SUCCESS");
        fields.put("timestamp", "1790648162");
        String canonical = "amount=1500&gatewayTradeNo=MOCK20260929101602483911&nonce=9f2c7a1e4b6d8f03&orderNo=O202609291015004821&paymentNo=P202609291015304107&status=SUCCESS&timestamp=1790648162";
        assertThat(PaySigner.canonical(fields)).isEqualTo(canonical);
        assertThat(PaySigner.sign(fields, "example-secret-do-not-use-anywhere-0001"))
                .isEqualTo("6f9ae15df7364522ef981a8fc3ce4ed861db81dffe5c7dbba44dde67292316a2");
    }
}
