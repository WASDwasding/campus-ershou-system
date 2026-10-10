package com.campus.ershou.im;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.im.dto.ReadNotificationsRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final ImHub imHub;

    public NotificationService(NotificationMapper notificationMapper, ImHub imHub) {
        this.notificationMapper = notificationMapper;
        this.imHub = imHub;
    }

    public void notifyVerificationApproved(long userId) {
        insert(userId, "VERIFICATION_APPROVED", "校园认证已通过", "你已通过校园认证，可以发布商品。");
    }

    public void notifyVerificationRejected(long userId, String reason) {
        insert(userId, "VERIFICATION_REJECTED", "校园认证未通过", reason);
    }

    public void notify(long userId, String type, String title, String content, String refType, Long refId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notificationMapper.insert(notification);
        imHub.push(userId, Map.of("type", "NOTIFICATION", "notificationType", type, "refType", refType == null ? "" : refType, "refId", refId == null ? 0 : refId));
        imHub.push(userId, Map.of("type", "BADGE"));
    }

    public long countUnread(long userId) {
        return notificationMapper.countUnread(userId);
    }

    public Map<String, Object> list(long userId, Boolean unreadOnly, Integer page, Integer size) {
        int[] paging = Paging.page(page, size);
        boolean onlyUnread = Boolean.TRUE.equals(unreadOnly);
        int offset = (paging[0] - 1) * paging[1];
        List<Map<String, Object>> list = notificationMapper.list(userId, onlyUnread, offset, paging[1]).stream()
                .map(NotificationService::toView)
                .toList();
        long total = notificationMapper.count(userId, onlyUnread);
        return Paging.of(list, paging[0], paging[1], total);
    }

    public Map<String, Object> markRead(long userId, ReadNotificationsRequest request) {
        int updated;
        if (Boolean.TRUE.equals(request.all())) {
            updated = notificationMapper.markAllRead(userId);
        } else if (request.ids() == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("ids", "请传入 ids 或 all")));
        } else if (request.ids().stream().anyMatch(Objects::isNull)) {
            throw ApiException.validation(List.of(new FieldErrorItem("ids", "通知 ID 不合法")));
        } else if (request.ids().isEmpty()) {
            updated = 0;
        } else {
            updated = notificationMapper.markRead(userId, request.ids());
        }
        return Map.of("updated", updated);
    }

    private void insert(long userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notificationMapper.insert(notification);
        imHub.push(userId, Map.of("type", "NOTIFICATION", "notificationType", type));
        imHub.push(userId, Map.of("type", "BADGE"));
    }

    private static Map<String, Object> toView(Notification notification) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", notification.getId());
        data.put("type", notification.getType());
        data.put("title", notification.getTitle());
        data.put("content", notification.getContent());
        if (notification.getRefType() == null || notification.getRefId() == null) {
            data.put("ref", null);
        } else {
            data.put("ref", Map.of("type", notification.getRefType(), "id", notification.getRefId()));
        }
        data.put("read", notification.getReadAt() != null);
        data.put("createdAt", at(notification.getCreatedAt()));
        return data;
    }

    private static OffsetDateTime at(LocalDateTime time) {
        return time == null ? null : time.atOffset(ZoneOffset.ofHours(8));
    }
}
