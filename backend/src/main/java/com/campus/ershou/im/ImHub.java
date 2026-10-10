package com.campus.ershou.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ImHub {

    private static final Logger log = LoggerFactory.getLogger(ImHub.class);

    private final ObjectMapper objectMapper;
    private final Map<Long, CopyOnWriteArrayList<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public ImHub(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean add(long userId, WebSocketSession session) {
        CopyOnWriteArrayList<WebSocketSession> list = sessions.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>());
        if (list.size() >= 3) {
            return false;
        }
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("seenAt", Instant.now());
        list.add(session);
        return true;
    }

    public void remove(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        if (!(userId instanceof Long id)) {
            return;
        }
        CopyOnWriteArrayList<WebSocketSession> list = sessions.get(id);
        if (list == null) {
            return;
        }
        list.remove(session);
        if (list.isEmpty()) {
            sessions.remove(id, list);
        }
    }

    public void touch(WebSocketSession session) {
        session.getAttributes().put("seenAt", Instant.now());
    }

    public void push(long userId, Map<String, Object> event) {
        CopyOnWriteArrayList<WebSocketSession> list = sessions.get(userId);
        if (list == null || list.isEmpty()) {
            return;
        }
        String text;
        try {
            text = objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            log.warn("IM 推送序列化失败");
            return;
        }
        TextMessage message = new TextMessage(text);
        for (WebSocketSession session : list) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (Exception ex) {
                log.warn("IM 推送失败 userId={}", userId);
            }
        }
    }

    public void pushAll(List<Long> userIds, Map<String, Object> event) {
        for (Long userId : userIds) {
            if (userId != null) {
                push(userId, event);
            }
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void closeIdle() {
        Instant deadline = Instant.now().minusSeconds(90);
        sessions.forEach((userId, list) -> {
            for (WebSocketSession session : list) {
                Object seen = session.getAttributes().get("seenAt");
                if (seen instanceof Instant instant && instant.isBefore(deadline) && session.isOpen()) {
                    try {
                        session.close(CloseStatus.GOING_AWAY);
                    } catch (Exception ignored) {
                        remove(session);
                    }
                }
            }
        });
    }
}
