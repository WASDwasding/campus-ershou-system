package com.campus.ershou.im;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.security.JwtService;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ImWebSocketHandler extends TextWebSocketHandler {

    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final StringRedisTemplate redis;
    private final ImHub imHub;
    private final ObjectMapper objectMapper;

    public ImWebSocketHandler(
            JwtService jwtService,
            UserMapper userMapper,
            StringRedisTemplate redis,
            ImHub imHub,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.redis = redis;
        this.imHub = imHub;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String header = session.getHandshakeHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            session.close(new CloseStatus(4401));
            return;
        }
        LoginUser loginUser;
        try {
            Object principal = jwtService.parsePrincipal(header.substring(7).trim());
            if (!(principal instanceof LoginUser user)) {
                session.close(new CloseStatus(4401));
                return;
            }
            loginUser = user;
        } catch (ApiException ex) {
            session.close(new CloseStatus(4401));
            return;
        }
        if (Boolean.TRUE.equals(redis.hasKey(RedisKeys.jwtDeny(loginUser.jti())))) {
            session.close(new CloseStatus(4401));
            return;
        }
        UserEntity user = userMapper.findById(loginUser.userId());
        if (user == null || user.getTokenVersion() != loginUser.tokenVersion()) {
            session.close(new CloseStatus(4401));
            return;
        }
        if ("BANNED".equals(user.getStatus())) {
            session.close(new CloseStatus(4403));
            return;
        }
        if (!imHub.add(loginUser.userId(), session)) {
            session.close(new CloseStatus(4429));
            return;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        imHub.touch(session);
        JsonNode node = objectMapper.readTree(message.getPayload());
        if (node != null && "PING".equals(node.path("type").asText())) {
            synchronized (session) {
                session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        imHub.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        imHub.remove(session);
    }
}
