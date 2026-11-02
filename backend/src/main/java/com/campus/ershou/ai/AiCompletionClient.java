package com.campus.ershou.ai;

import com.campus.ershou.common.config.AppProperties;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AiCompletionClient {

    private static final Logger log = LoggerFactory.getLogger(AiCompletionClient.class);
    private static final Duration CAP = Duration.ofSeconds(8);

    private final AppProperties props;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final AtomicBoolean missingWarned = new AtomicBoolean();

    public AiCompletionClient(AppProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public Optional<String> complete(String system, List<AiPart> userParts, int maxTokens) {
        if (!ready()) {
            return Optional.empty();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint()))
                    .timeout(callTimeout())
                    .header("Authorization", "Bearer " + props.getAi().getApiKey().trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body(system, userParts, maxTokens)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("ai upstream status={}", response.statusCode());
                return Optional.empty();
            }
            return content(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("ai upstream interrupted");
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("ai upstream failed: {}", ex.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    private boolean ready() {
        AppProperties.Ai ai = props.getAi();
        boolean ready = ai.isEnabled() && text(ai.getApiKey()) && text(ai.getBaseUrl()) && text(ai.getModel());
        if (!ready && ai.isEnabled() && missingWarned.compareAndSet(false, true)) {
            log.warn("AI 已开启但地址、密钥或模型名为空，改为规则结果");
        }
        return ready;
    }

    private Duration callTimeout() {
        Duration configured = props.getAi().getTimeout();
        if (configured == null || configured.isZero() || configured.isNegative() || configured.compareTo(CAP) > 0) {
            return CAP;
        }
        return configured;
    }

    private String endpoint() {
        String base = props.getAi().getBaseUrl().trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/chat/completions";
    }

    private String body(String system, List<AiPart> userParts, int maxTokens) throws JacksonException {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", props.getAi().getModel().trim());
        root.put("enable_thinking", false);
        root.put("temperature", 0.2);
        root.put("max_tokens", maxTokens);
        ArrayNode messages = root.putArray("messages");
        messages.addObject().put("role", "system").put("content", system);
        ObjectNode user = messages.addObject();
        user.put("role", "user");
        ArrayNode content = user.putArray("content");
        for (AiPart part : userParts) {
            if (part.jpeg() != null) {
                ObjectNode image = content.addObject();
                image.put("type", "image_url");
                image.putObject("image_url")
                        .put("url", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(part.jpeg()));
            } else if (part.text() != null) {
                content.addObject().put("type", "text").put("text", part.text());
            }
        }
        return objectMapper.writeValueAsString(root);
    }

    private Optional<String> content(String raw) {
        try {
            JsonNode content = objectMapper.readTree(raw).path("choices").path(0).path("message").path("content");
            if (content.isTextual()) {
                String text = content.asText();
                return text.isBlank() ? Optional.empty() : Optional.of(text);
            }
            if (content.isArray()) {
                StringBuilder joined = new StringBuilder();
                for (JsonNode part : content) {
                    if (part.isTextual()) {
                        joined.append(part.asText());
                    } else if (part.path("text").isTextual()) {
                        joined.append(part.path("text").asText());
                    }
                }
                String text = joined.toString();
                return text.isBlank() ? Optional.empty() : Optional.of(text);
            }
            return Optional.empty();
        } catch (JacksonException ex) {
            return Optional.empty();
        }
    }

    private static boolean text(String value) {
        return value != null && !value.isBlank();
    }
}
