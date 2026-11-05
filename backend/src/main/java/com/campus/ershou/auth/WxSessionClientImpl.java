package com.campus.ershou.auth;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class WxSessionClientImpl implements WxSessionClient {

    private static final Logger log = LoggerFactory.getLogger(WxSessionClientImpl.class);

    private final AppProperties props;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public WxSessionClientImpl(AppProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    @Override
    public String exchange(String code) {
        String appId = props.getWx().getAppId() == null ? "" : props.getWx().getAppId().trim();
        String secret = props.getWx().getAppSecret() == null ? "" : props.getWx().getAppSecret().trim();
        if (appId.isEmpty() || secret.isEmpty()) {
            throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + encode(appId)
                + "&secret=" + encode(secret)
                + "&js_code=" + encode(code)
                + "&grant_type=authorization_code";
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("wx session status={}", response.statusCode());
                throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
            }
            JsonNode root = objectMapper.readTree(response.body());
            int errcode = root.path("errcode").asInt(0);
            if (errcode != 0) {
                log.warn("wx session errcode={}", errcode);
                if (errcode == 40029 || errcode == 40163) {
                    throw ApiException.of(ErrorCode.WX_LOGIN_FAILED);
                }
                throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
            }
            String openid = root.path("openid").asText("");
            if (openid.isBlank()) {
                throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
            }
            return openid;
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
        } catch (Exception ex) {
            log.warn("wx session failed: {}", ex.getClass().getSimpleName());
            throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
