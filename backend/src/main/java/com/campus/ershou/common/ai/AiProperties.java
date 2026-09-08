package com.campus.ershou.common.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@Data
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String baseUrl;
    private String apiKey;
    private String model;

    public boolean configured() {
        return StringUtils.hasText(apiKey);
    }
}
