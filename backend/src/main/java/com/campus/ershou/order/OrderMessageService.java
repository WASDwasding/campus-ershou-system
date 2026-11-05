package com.campus.ershou.order;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.im.ImHub;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.user.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderMessageService {

    private final OrderMessageMapper messageMapper;
    private final OrderMapper orderMapper;
    private final DisputeMapper disputeMapper;
    private final OrderService orderService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ImHub imHub;
    private final RateLimitService rateLimitService;

    public OrderMessageService(
            OrderMessageMapper messageMapper,
            OrderMapper orderMapper,
            DisputeMapper disputeMapper,
            OrderService orderService,
            UserService userService,
            NotificationService notificationService,
            AuditService auditService,
            ImHub imHub,
            RateLimitService rateLimitService
    ) {
        this.messageMapper = messageMapper;
        this.orderMapper = orderMapper;
        this.disputeMapper = disputeMapper;
        this.orderService = orderService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.imHub = imHub;
        this.rateLimitService = rateLimitService;
    }

    public Map<String, Object> list(long userId, long orderId, Long beforeId, Long afterId, Integer size, String ip) {
        if (beforeId != null && afterId != null) {
            throw ApiException.validation(List.of(new FieldErrorItem("beforeId", "beforeId 和 afterId 只能带一个")));
        }
        OrderRecord order = orderService.syncParty(userId, orderId, ip);
        int limit = pageSize(size);
        boolean canSend = "PAID".equals(order.getStatus()) || "DISPUTED".equals(order.getStatus());
        if ("PENDING_PAY".equals(order.getStatus())) {
            return Map.of("canSend", false, "list", List.of(), "hasMore", false);
        }
        List<OrderMessage> rows = load(orderId, beforeId, afterId, limit);
        boolean hasMore = rows.size() > limit;
        if (hasMore) {
            rows = new ArrayList<>(rows.subList(0, limit));
        }
        if (beforeId != null || afterId == null) {
            Collections.reverse(rows);
        }
        if (!rows.isEmpty() && beforeId == null && afterId == null) {
            long max = rows.get(rows.size() - 1).getId();
            if (order.getBuyerId() != null && order.getBuyerId() == userId) {
                orderMapper.markBuyerRead(orderId, userId, max);
            } else if (order.getSellerId() != null && order.getSellerId() == userId) {
                orderMapper.markSellerRead(orderId, userId, max);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderMessage message : rows) {
            list.add(view(message));
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("canSend", canSend);
        data.put("list", list);
        data.put("hasMore", hasMore);
        return data;
    }

    public Map<String, Object> send(long userId, long orderId, String content, String clientMsgId, String ip) {
        rateLimitService.check("order-message", String.valueOf(userId), 30, Duration.ofMinutes(1));
        OrderRecord order = orderService.syncParty(userId, orderId, ip);
        if ("PENDING_PAY".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.CHAT_NOT_OPEN, "支付完成并进入待面交后才能聊天");
        }
        if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.CHAT_READONLY);
        }
        if (!"PAID".equals(order.getStatus()) && !"DISPUTED".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.CHAT_NOT_OPEN);
        }
        if (!"VERIFIED".equals(userService.requireUser(userId).getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        String text = content == null ? "" : content.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 1 || length > 500) {
            throw ApiException.validation(List.of(new FieldErrorItem("content", "消息长度为 1–500")));
        }
        if (ContactInfo.contains(text)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        if (clientMsgId == null || !clientMsgId.matches("[A-Za-z0-9_-]{8,40}")) {
            throw ApiException.validation(List.of(new FieldErrorItem("clientMsgId", "clientMsgId 为 8–40 位")));
        }
        OrderMessage existing = messageMapper.findByClient(orderId, userId, clientMsgId);
        if (existing != null) {
            return view(existing);
        }
        OrderMessage message = new OrderMessage();
        message.setOrderId(orderId);
        message.setSenderId(userId);
        message.setType("TEXT");
        message.setContent(text);
        message.setClientMsgId(clientMsgId);
        try {
            messageMapper.insert(message);
        } catch (DuplicateKeyException ex) {
            OrderMessage raced = messageMapper.findByClient(orderId, userId, clientMsgId);
            if (raced != null) {
                return view(raced);
            }
            throw ex;
        }
        long other = order.getBuyerId() != null && order.getBuyerId() == userId ? order.getSellerId() : order.getBuyerId();
        notificationService.notify(other, "CHAT_MESSAGE", "新消息", "你有一条新的订单消息", "ORDER", orderId);
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("type", "MESSAGE");
        event.put("orderId", orderId);
        event.put("message", view(message));
        imHub.push(other, event);
        return view(message);
    }

    public Map<String, Object> adminList(long disputeId, String ip) {
        DisputeRecord dispute = disputeMapper.findById(disputeId);
        if (dispute == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!"OPEN".equals(dispute.getStatus())) {
            auditService.record("SECURITY", "ACCESS_DENIED", null, null, ip, "{\"disputeId\":" + disputeId + "}");
            throw ApiException.status(ErrorCode.CHAT_NOT_OPEN, HttpStatus.FORBIDDEN);
        }
        List<OrderMessage> rows = messageMapper.latest(dispute.getOrderId(), 50);
        Collections.reverse(rows);
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderMessage message : rows) {
            list.add(view(message));
        }
        return Map.of("canSend", false, "list", list);
    }

    private List<OrderMessage> load(long orderId, Long beforeId, Long afterId, int limit) {
        int query = limit + 1;
        if (beforeId != null) {
            return messageMapper.before(orderId, beforeId, query);
        }
        if (afterId != null) {
            return messageMapper.after(orderId, afterId, query);
        }
        return messageMapper.latest(orderId, query);
    }

    private static int pageSize(Integer size) {
        if (size == null) {
            return 30;
        }
        if (size < 1 || size > 50) {
            throw ApiException.validation(List.of(new FieldErrorItem("size", "size 为 1–50")));
        }
        return size;
    }

    private static Map<String, Object> view(OrderMessage message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", message.getId());
        data.put("orderId", message.getOrderId());
        data.put("senderId", message.getSenderId());
        data.put("type", message.getType());
        data.put("content", message.getContent());
        data.put("createdAt", Times.at(message.getCreatedAt()));
        return data;
    }
}
