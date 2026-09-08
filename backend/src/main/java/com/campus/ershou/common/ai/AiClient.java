package com.campus.ershou.common.ai;

import com.campus.ershou.common.exception.BizException;
import org.springframework.stereotype.Service;

/**
 * 大模型调用入口。业务实现定价建议、议价客服时从这里走，禁止在 Controller 里直接拼 HTTP。
 */
@Service
public class AiClient {

    private final AiProperties properties;

    public AiClient(AiProperties properties) {
        this.properties = properties;
    }

    public String chat(String systemPrompt, String userMessage) {
        if (!properties.configured()) {
            throw new BizException("大模型 API Key 未配置");
        }
        throw new BizException(501, "AI 调用尚未实现");
    }
}
