package com.campus.ershou.order;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.file.FileService;
import com.campus.ershou.common.file.FileUrls;
import com.campus.ershou.common.rate.RateLimitService;
import com.campus.ershou.common.redis.GoodsLock;
import com.campus.ershou.common.redis.IdempotencyStore;
import com.campus.ershou.common.util.ContactInfo;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.Times;
import com.campus.ershou.goods.GoodsImageRow;
import com.campus.ershou.goods.GoodsMapper;
import com.campus.ershou.goods.GoodsRow;
import com.campus.ershou.im.ConversationService;
import com.campus.ershou.im.ImHub;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.pay.PaymentMapper;
import com.campus.ershou.pay.PaymentRecord;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private static final Set<String> STATUSES = Set.of("PENDING_PAY", "PAID", "COMPLETED", "CANCELLED", "DISPUTED");
    private static final Set<String> DISPUTE_REASONS = Set.of("NOT_AS_DESCRIBED", "NO_SHOW", "DAMAGED", "OTHER");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final OrderTx orderTx;
    private final GoodsMapper goodsMapper;
    private final PaymentMapper paymentMapper;
    private final DisputeMapper disputeMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final BuildingMapper buildingMapper;
    private final FileService fileService;
    private final FileUrls fileUrls;
    private final ConversationService conversationService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ImHub imHub;
    private final RateLimitService rateLimitService;
    private final GoodsLock goodsLock;
    private final IdempotencyStore idempotencyStore;
    private final StringRedisTemplate redis;
    private final AppProperties props;

    public OrderService(
            OrderMapper orderMapper,
            OrderTx orderTx,
            GoodsMapper goodsMapper,
            PaymentMapper paymentMapper,
            DisputeMapper disputeMapper,
            ReviewMapper reviewMapper,
            UserMapper userMapper,
            UserService userService,
            BuildingMapper buildingMapper,
            FileService fileService,
            FileUrls fileUrls,
            ConversationService conversationService,
            NotificationService notificationService,
            AuditService auditService,
            ImHub imHub,
            RateLimitService rateLimitService,
            GoodsLock goodsLock,
            IdempotencyStore idempotencyStore,
            StringRedisTemplate redis,
            AppProperties props
    ) {
        this.orderMapper = orderMapper;
        this.orderTx = orderTx;
        this.goodsMapper = goodsMapper;
        this.paymentMapper = paymentMapper;
        this.disputeMapper = disputeMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.buildingMapper = buildingMapper;
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.conversationService = conversationService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.imHub = imHub;
        this.rateLimitService = rateLimitService;
        this.goodsLock = goodsLock;
        this.idempotencyStore = idempotencyStore;
        this.redis = redis;
        this.props = props;
    }

    public Map<String, Object> create(long userId, Long goodsId, Integer quantity, String idempotencyKey, String ip) {
        requireVerified(userId);
        rateLimitService.check("order-create", String.valueOf(userId), 10, Duration.ofHours(1));
        return idempotencyStore.run(userId, idempotencyKey, () -> createOnce(userId, goodsId, quantity, ip));
    }

    private Map<String, Object> createOnce(long userId, Long goodsId, Integer quantity, String ip) {
        if (goodsId == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("goodsId", "请选择商品")));
        }
        if (quantity == null || quantity != 1) {
            throw ApiException.validation(List.of(new FieldErrorItem("quantity", "数量只能是 1")));
        }
        GoodsRow goods = goodsMapper.findById(goodsId);
        if (goods == null || "DELETED".equals(goods.getStatus())) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (goods.getSellerId() != null && goods.getSellerId() == userId) {
            throw ApiException.of(ErrorCode.CANNOT_BUY_OWN_GOODS);
        }
        if (orderMapper.countPendingPay(userId) >= 3) {
            throw ApiException.of(ErrorCode.PENDING_ORDER_LIMIT);
        }
        assertNotRestricted(userId);
        if (!"ON_SALE".equals(goods.getStatus())) {
            throw unavailable(goodsId, userId, ip);
        }
        String lock = goodsLock.tryLock(goodsId);
        if (lock == null) {
            throw unavailable(goodsId, userId, ip);
        }
        try {
            OrderRecord order = new OrderRecord();
            order.setBuyerId(userId);
            order.setSellerId(goods.getSellerId());
            order.setGoodsId(goodsId);
            order.setStatus("PENDING_PAY");
            order.setAmount(goods.getPrice());
            order.setQuantity(1);
            order.setGoodsTitle(goods.getTitle());
            order.setGoodsCoverFileId(coverFileId(goodsId));
            order.setExpireAt(LocalDateTime.now(Times.SHANGHAI).plus(props.getPay().getTimeout()));
            OrderRecord saved = insertWithRetry(order);
            afterCreate(saved);
            auditService.record("BUSINESS", "ORDER_STATUS_CHANGED", null, userId, ip,
                    "{\"orderId\":" + saved.getId() + ",\"to\":\"PENDING_PAY\"}");
            return detail(userId, saved.getId(), ip);
        } catch (ApiException ex) {
            if (ex.getErrorCode() == ErrorCode.GOODS_NOT_AVAILABLE) {
                throw unavailable(goodsId, userId, ip);
            }
            throw ex;
        } finally {
            goodsLock.unlock(goodsId, lock);
        }
    }

    public Map<String, Object> detail(long userId, long orderId, String ip) {
        OrderRecord order = orderMapper.findById(orderId);
        if (order == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!isParty(order, userId)) {
            auditService.record("SECURITY", "ACCESS_DENIED", null, userId, ip, "{\"orderId\":" + orderId + "}");
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        order = expireIfNeeded(order);
        return toDetail(order, userId);
    }

    public Map<String, Object> listMine(long userId, String role, String status, Integer page, Integer size) {
        userService.requireUser(userId);
        String normalizedRole = role == null || role.isBlank() ? "BUYER" : role.trim().toUpperCase();
        if (!"BUYER".equals(normalizedRole) && !"SELLER".equals(normalizedRole)) {
            throw ApiException.validation(List.of(new FieldErrorItem("role", "只能是 BUYER 或 SELLER")));
        }
        List<String> statuses = parseStatuses(status);
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderRecord order : orderMapper.listMine(userId, normalizedRole, statuses, offset, paging[1])) {
            list.add(toCard(expireIfNeeded(order), userId));
        }
        return Paging.of(list, paging[0], paging[1], orderMapper.countMine(userId, normalizedRole, statuses));
    }

    public Map<String, Object> cancel(long userId, long orderId, String reason, String ip) {
        OrderRecord order = requireParty(userId, orderId, ip);
        order = expireIfNeeded(order);
        String clean = cleanReason(reason);
        if ("PENDING_PAY".equals(order.getStatus())) {
            String by = order.getBuyerId() == userId ? "BUYER" : "SELLER";
            orderTx.cancelUnpaid(order, by, userId, clean);
            afterCancel(order, "订单已取消");
        } else if ("PAID".equals(order.getStatus())) {
            if (order.getSellerId() != userId) {
                throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
            }
            orderTx.cancelPaid(order, "SELLER", userId, clean);
            afterCancel(order, "卖家已取消，款项已退回");
        } else {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        auditService.record("BUSINESS", "ORDER_STATUS_CHANGED", null, userId, ip,
                "{\"orderId\":" + orderId + ",\"to\":\"CANCELLED\"}");
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> meetup(long userId, long orderId, Long buildingId, String place, OffsetDateTime time, String ip) {
        requireVerified(userId);
        OrderRecord order = requireParty(userId, orderId, ip);
        if (buildingId == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("buildingId", "请选择楼栋")));
        }
        BuildingView building = buildingMapper.findEnabledById(buildingId);
        if (building == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("buildingId", "楼栋不存在")));
        }
        String clean = place == null ? "" : place.trim();
        int length = clean.codePointCount(0, clean.length());
        if (length < 1 || length > 40) {
            throw ApiException.validation(List.of(new FieldErrorItem("place", "地点 1–40 字")));
        }
        if (ContactInfo.contains(clean)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        if (time == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("time", "请选择时间")));
        }
        LocalDateTime local = Times.toShanghai(time);
        LocalDateTime now = LocalDateTime.now(Times.SHANGHAI);
        if (local.isBefore(now) || local.isAfter(now.plusDays(14))) {
            throw ApiException.validation(List.of(new FieldErrorItem("time", "时间须在未来 14 天内")));
        }
        orderTx.updateMeetup(order.getId(), buildingId, clean, local);
        if (order.getConversationId() != null) {
            conversationService.system(order.getConversationId(), "面交信息已更新");
        }
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> handover(long userId, long orderId, String ip) {
        requireVerified(userId);
        OrderRecord order = requireParty(userId, orderId, ip);
        boolean buyer = order.getBuyerId() == userId;
        boolean completed = orderTx.confirmHandover(order, buyer);
        if (completed) {
            if (order.getConversationId() != null) {
                conversationService.system(order.getConversationId(), "双方已确认，交易完成");
            }
            notifyBoth(order, "ORDER_COMPLETED", "交易已完成", "双方已确认面交。");
            auditService.record("BUSINESS", "ORDER_STATUS_CHANGED", null, userId, ip,
                    "{\"orderId\":" + orderId + ",\"to\":\"COMPLETED\"}");
        }
        pushStatus(order);
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> dispute(long userId, long orderId, String reason, String description, List<String> evidence, String ip) {
        requireVerified(userId);
        OrderRecord order = requireParty(userId, orderId, ip);
        String cleanReason = reason == null ? "" : reason.trim().toUpperCase();
        if (!DISPUTE_REASONS.contains(cleanReason)) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "申诉原因不合法")));
        }
        String text = description == null ? "" : description.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 10 || length > 500) {
            throw ApiException.validation(List.of(new FieldErrorItem("description", "说明 10–500 字")));
        }
        if (ContactInfo.contains(text)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        List<String> files = evidence == null ? List.of() : evidence;
        if (files.size() > 6) {
            throw ApiException.validation(List.of(new FieldErrorItem("evidenceFileIds", "最多 6 张")));
        }
        for (String fileId : files) {
            fileService.requireUsable(fileId, userId, "EVIDENCE");
        }
        orderTx.openDispute(order, userId, cleanReason, text, files);
        if (order.getConversationId() != null) {
            conversationService.system(order.getConversationId(), "已发起申诉");
        }
        notifyBoth(order, "ORDER_DISPUTED", "订单申诉中", "有一方发起了申诉。");
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> withdrawDispute(long userId, long orderId, String ip) {
        OrderRecord order = requireParty(userId, orderId, ip);
        DisputeRecord dispute = disputeMapper.findOpen(orderId);
        if (dispute == null || dispute.getOpenerId() == null || dispute.getOpenerId() != userId) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        orderTx.withdrawDispute(order, dispute);
        if (order.getConversationId() != null) {
            conversationService.system(order.getConversationId(), "申诉已撤回");
        }
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> review(long userId, long orderId, Integer score, String content, String ip) {
        requireVerified(userId);
        OrderRecord order = requireParty(userId, orderId, ip);
        if (!"COMPLETED".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        if (order.getCompletedAt() == null || order.getCompletedAt().plusDays(30).isBefore(LocalDateTime.now(Times.SHANGHAI))) {
            throw ApiException.of(ErrorCode.REVIEW_WINDOW_CLOSED);
        }
        if (score == null || score < 1 || score > 5) {
            throw ApiException.validation(List.of(new FieldErrorItem("score", "评分 1–5")));
        }
        String text = content == null ? "" : content.trim();
        if (text.codePointCount(0, text.length()) > 200) {
            throw ApiException.validation(List.of(new FieldErrorItem("content", "评价最多 200 字")));
        }
        if (!text.isEmpty() && ContactInfo.contains(text)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        ReviewRecord review = new ReviewRecord();
        review.setOrderId(orderId);
        review.setGoodsId(order.getGoodsId());
        review.setReviewerId(userId);
        review.setRevieweeId(order.getBuyerId() == userId ? order.getSellerId() : order.getBuyerId());
        review.setScore(score);
        review.setContent(text.isEmpty() ? null : text);
        orderTx.review(review);
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> adminList(String status, String orderNo, Long buyerId, Long sellerId, Integer page, Integer size) {
        String normalized = blankToNull(status);
        if (normalized != null && !STATUSES.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "订单状态不合法")));
        }
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderRecord order : orderMapper.adminList(normalized, blankToNull(orderNo), buyerId, sellerId, offset, paging[1])) {
            list.add(toCard(order, null));
        }
        return Paging.of(list, paging[0], paging[1], orderMapper.adminCount(normalized, blankToNull(orderNo), buyerId, sellerId));
    }

    public Map<String, Object> adminDetail(long orderId) {
        OrderRecord order = orderMapper.findById(orderId);
        if (order == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        Map<String, Object> data = toDetail(order, null);
        List<Map<String, Object>> events = new ArrayList<>();
        for (OrderEvent event : orderMapper.listEvents(orderId)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fromStatus", event.getFromStatus());
            item.put("toStatus", event.getToStatus());
            item.put("actorType", event.getActorType());
            item.put("actorId", event.getActorId());
            item.put("createdAt", Times.at(event.getCreatedAt()));
            events.add(item);
        }
        data.put("events", events);
        return data;
    }

    public Map<String, Object> adminDisputes(String status, Integer page, Integer size) {
        String normalized = blankToNull(status);
        if (normalized != null && !Set.of("OPEN", "WITHDRAWN", "RESOLVED").contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("status", "申诉状态不合法")));
        }
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<Map<String, Object>> list = new ArrayList<>();
        for (DisputeRecord dispute : disputeMapper.adminList(normalized, offset, paging[1])) {
            list.add(disputeView(dispute));
        }
        return Paging.of(list, paging[0], paging[1], disputeMapper.adminCount(normalized));
    }

    public Map<String, Object> resolve(long adminId, long disputeId, String result, String remark, String ip) {
        String outcome = result == null ? "" : result.trim().toUpperCase();
        if (!"COMPLETE".equals(outcome) && !"REFUND".equals(outcome)) {
            throw ApiException.validation(List.of(new FieldErrorItem("result", "只能是 COMPLETE 或 REFUND")));
        }
        String text = remark == null ? "" : remark.trim();
        int length = text.codePointCount(0, text.length());
        if (length < 2 || length > 300) {
            throw ApiException.validation(List.of(new FieldErrorItem("remark", "备注 2–300 字")));
        }
        DisputeRecord dispute = disputeMapper.findById(disputeId);
        if (dispute == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        OrderRecord order = orderMapper.findById(dispute.getOrderId());
        if (order == null || !"OPEN".equals(dispute.getStatus())) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        if ("COMPLETE".equals(outcome)) {
            orderTx.resolveComplete(order, dispute, text);
            if (order.getConversationId() != null) {
                conversationService.system(order.getConversationId(), "管理员已判定交易完成");
            }
        } else {
            orderTx.resolveRefund(order, dispute, text);
            if (order.getConversationId() != null) {
                conversationService.system(order.getConversationId(), "管理员已判定退款");
            }
        }
        notifyBoth(order, "ORDER_DISPUTE_RESOLVED", "申诉已处理", text);
        auditService.record("ADMIN", "ADMIN_DISPUTE_RESOLVE", adminId, order.getBuyerId(), ip,
                "{\"disputeId\":" + disputeId + ",\"orderId\":" + order.getId() + ",\"result\":\"" + outcome + "\"}");
        return adminDetail(order.getId());
    }

    public void cancelPendingOf(long userId, String reason) {
        for (OrderRecord order : orderMapper.listPendingOfUser(userId)) {
            orderTx.cancelUnpaid(order, "ADMIN", null, reason);
            afterCancel(order, "订单已取消");
        }
    }

    public OrderRecord requireParty(long userId, long orderId, String ip) {
        OrderRecord order = orderMapper.findById(orderId);
        if (order == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!isParty(order, userId)) {
            auditService.record("SECURITY", "ACCESS_DENIED", null, userId, ip, "{\"orderId\":" + orderId + "}");
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        return order;
    }

    public int countBuyerPending(long userId) {
        return orderMapper.countPendingPay(userId);
    }

    public int countSellerActive(long userId) {
        return orderMapper.countSellerActive(userId);
    }

    public int countBuyerTodos(long userId) {
        return orderMapper.countBuyerTodos(userId);
    }

    public long countOpenDisputes() {
        return disputeMapper.countOpen();
    }

    public OrderRecord findActive(long goodsId) {
        return orderMapper.findActiveByGoods(goodsId);
    }

    public Double rating(long userId) {
        String raw = reviewMapper.averageScore(userId);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Double.valueOf(raw);
    }

    public Map<String, Object> reviewsOf(long userId, Integer page, Integer size) {
        if (userMapper.findById(userId) == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        int[] paging = Paging.page(page, size);
        int offset = (paging[0] - 1) * paging[1];
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReviewRecord review : reviewMapper.listForUser(userId, offset, paging[1])) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", review.getId());
            item.put("orderId", review.getOrderId());
            item.put("goodsId", review.getGoodsId());
            item.put("score", review.getScore());
            item.put("content", review.getContent());
            item.put("reviewer", Map.of("id", review.getReviewerId(), "nickname", review.getReviewerNickname()));
            item.put("createdAt", Times.at(review.getCreatedAt()));
            list.add(item);
        }
        return Paging.of(list, paging[0], paging[1], reviewMapper.countForUser(userId));
    }

    @Scheduled(fixedDelay = 30000)
    public void expireDue() {
        for (OrderRecord order : orderMapper.listExpired()) {
            try {
                expireIfNeeded(order);
            } catch (RuntimeException ignored) {
                // 下一轮再试
            }
        }
    }

    private OrderRecord insertWithRetry(OrderRecord order) {
        DuplicateKeyException last = null;
        for (int i = 0; i < 3; i++) {
            order.setOrderNo("O" + STAMP.format(LocalDateTime.now(Times.SHANGHAI)) + Randoms.digits(4));
            try {
                return orderTx.create(order);
            } catch (DuplicateKeyException ex) {
                last = ex;
            }
        }
        throw last;
    }

    private OrderRecord expireIfNeeded(OrderRecord order) {
        if (!"PENDING_PAY".equals(order.getStatus()) || order.getExpireAt() == null) {
            return order;
        }
        if (order.getExpireAt().isAfter(LocalDateTime.now(Times.SHANGHAI))) {
            return order;
        }
        try {
            orderTx.cancelUnpaid(order, "TIMEOUT", null, "超时未支付");
        } catch (ApiException ex) {
            if (ex.getErrorCode() != ErrorCode.ORDER_STATUS_INVALID) {
                throw ex;
            }
        }
        String key = RedisKeys.orderTimeouts(order.getBuyerId());
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, Duration.ofHours(24));
        }
        afterCancel(order, "订单超时已取消");
        OrderRecord fresh = orderMapper.findById(order.getId());
        return fresh == null ? order : fresh;
    }

    private void afterCreate(OrderRecord order) {
        if (order.getConversationId() != null) {
            conversationService.system(order.getConversationId(), "买家已下单，请在 15 分钟内支付");
        }
        notificationService.notify(order.getSellerId(), "ORDER_CREATED", "有新订单", order.getGoodsTitle(), "ORDER", order.getId());
        pushStatus(order);
    }

    private void afterCancel(OrderRecord order, String systemText) {
        if (order.getConversationId() != null) {
            conversationService.system(order.getConversationId(), systemText);
        }
        notifyBoth(order, "ORDER_CANCELLED", "订单已取消", systemText);
        OrderRecord fresh = orderMapper.findById(order.getId());
        pushStatus(fresh == null ? order : fresh);
    }

    private void notifyBoth(OrderRecord order, String type, String title, String content) {
        notificationService.notify(order.getBuyerId(), type, title, content, "ORDER", order.getId());
        notificationService.notify(order.getSellerId(), type, title, content, "ORDER", order.getId());
    }

    private void pushStatus(OrderRecord order) {
        imHub.pushAll(List.of(order.getBuyerId(), order.getSellerId()),
                Map.of("type", "ORDER_STATUS", "orderId", order.getId(), "status", order.getStatus()));
    }

    private ApiException unavailable(long goodsId, long buyerId, String ip) {
        auditService.record("BUSINESS", "ORDER_STOCK_CONFLICT", null, buyerId, ip, "{\"goodsId\":" + goodsId + "}");
        OrderRecord active = orderMapper.findActiveForBuyer(goodsId, buyerId);
        if (active != null) {
            return ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE, Map.of("myOrderId", active.getId()));
        }
        return ApiException.of(ErrorCode.GOODS_NOT_AVAILABLE);
    }

    private void assertNotRestricted(long userId) {
        String raw = redis.opsForValue().get(RedisKeys.orderTimeouts(userId));
        if (raw == null) {
            return;
        }
        int count;
        try {
            count = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return;
        }
        if (count < 3) {
            return;
        }
        Long ttl = redis.getExpire(RedisKeys.orderTimeouts(userId), TimeUnit.SECONDS);
        OffsetDateTime until = OffsetDateTime.now(Times.OFFSET).plusSeconds(ttl == null || ttl < 0 ? 0 : ttl);
        throw ApiException.of(ErrorCode.ORDER_RESTRICTED, Map.of("until", until.toString()));
    }

    private Map<String, Object> toCard(OrderRecord order, Long viewerId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", order.getId());
        data.put("orderNo", order.getOrderNo());
        data.put("status", order.getStatus());
        data.put("amount", order.getAmount());
        data.put("myRole", viewerId == null ? null : (order.getBuyerId().equals(viewerId) ? "BUYER" : "SELLER"));
        Map<String, Object> goods = new LinkedHashMap<>();
        goods.put("id", order.getGoodsId());
        goods.put("title", order.getGoodsTitle());
        goods.put("coverUrl", order.getGoodsCoverFileId() == null ? null : fileUrls.publicUrl(order.getGoodsCoverFileId()));
        data.put("goods", goods);
        data.put("expireAt", Times.at(order.getExpireAt()));
        data.put("createdAt", Times.at(order.getCreatedAt()));
        return data;
    }

    private Map<String, Object> toDetail(OrderRecord order, Long viewerId) {
        Map<String, Object> data = toCard(order, viewerId);
        data.put("quantity", order.getQuantity());
        data.put("buyer", person(userMapper.findById(order.getBuyerId())));
        data.put("seller", person(userMapper.findById(order.getSellerId())));
        data.put("conversationId", order.getConversationId());
        if (order.getMeetupPlace() == null) {
            data.put("meetup", null);
        } else {
            BuildingView building = order.getMeetupBuildingId() == null ? null : buildingMapper.findEnabledById(order.getMeetupBuildingId());
            Map<String, Object> meetup = new LinkedHashMap<>();
            meetup.put("buildingId", order.getMeetupBuildingId());
            meetup.put("buildingName", building == null ? null : building.getName());
            meetup.put("place", order.getMeetupPlace());
            meetup.put("time", Times.at(order.getMeetupTime()));
            data.put("meetup", meetup);
        }
        Map<String, Object> handover = new LinkedHashMap<>();
        handover.put("buyerConfirmed", order.getBuyerHandoverAt() != null);
        handover.put("sellerConfirmed", order.getSellerHandoverAt() != null);
        data.put("handover", handover);
        data.put("payment", paymentView(paymentMapper.listByOrder(order.getId())));
        if (order.getCancelBy() == null) {
            data.put("cancel", null);
        } else {
            data.put("cancel", Map.of("by", order.getCancelBy(), "reason", order.getCancelReason() == null ? "" : order.getCancelReason()));
        }
        boolean refunded = paymentMapper.listByOrder(order.getId()).stream().anyMatch(item -> "REFUNDED".equals(item.getStatus()));
        data.put("refund", Map.of("refunded", refunded));
        DisputeRecord dispute = disputeMapper.findLatest(order.getId());
        data.put("dispute", dispute == null ? null : disputeView(dispute));
        data.put("review", reviewView(order, viewerId));
        data.put("allowedActions", viewerId == null ? List.of() : actions(order, viewerId));
        data.put("paidAt", Times.at(order.getPaidAt()));
        data.put("completedAt", Times.at(order.getCompletedAt()));
        data.put("cancelledAt", Times.at(order.getCancelledAt()));
        return data;
    }

    private Map<String, Object> paymentView(List<PaymentRecord> payments) {
        if (payments.isEmpty()) {
            return null;
        }
        PaymentRecord payment = payments.get(0);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paymentNo", payment.getPaymentNo());
        data.put("status", payment.getStatus());
        data.put("amount", payment.getAmount());
        data.put("channel", payment.getChannel());
        data.put("expireAt", Times.at(payment.getExpireAt()));
        return data;
    }

    private Map<String, Object> disputeView(DisputeRecord dispute) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", dispute.getId());
        data.put("orderId", dispute.getOrderId());
        data.put("openerId", dispute.getOpenerId());
        data.put("reason", dispute.getReason());
        data.put("description", dispute.getDescription());
        data.put("status", dispute.getStatus());
        data.put("result", dispute.getResult());
        data.put("remark", dispute.getRemark());
        data.put("evidenceFileIds", dispute.getId() == null ? List.of() : disputeMapper.listFiles(dispute.getId()));
        data.put("createdAt", Times.at(dispute.getCreatedAt()));
        data.put("resolvedAt", Times.at(dispute.getResolvedAt()));
        return data;
    }

    private Map<String, Object> reviewView(OrderRecord order, Long viewerId) {
        List<ReviewRecord> reviews = reviewMapper.listByOrder(order.getId());
        ReviewRecord mine = null;
        ReviewRecord peer = null;
        for (ReviewRecord review : reviews) {
            if (viewerId != null && review.getReviewerId() != null && review.getReviewerId().equals(viewerId)) {
                mine = review;
            } else {
                peer = review;
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("mine", mine == null ? null : reviewItem(mine));
        data.put("peer", peer == null ? null : reviewItem(peer));
        return data;
    }

    private Map<String, Object> reviewItem(ReviewRecord review) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("score", review.getScore());
        data.put("content", review.getContent());
        data.put("createdAt", Times.at(review.getCreatedAt()));
        return data;
    }

    private List<String> actions(OrderRecord order, long userId) {
        List<String> actions = new ArrayList<>();
        boolean buyer = order.getBuyerId() == userId;
        switch (order.getStatus()) {
            case "PENDING_PAY" -> {
                if (buyer) {
                    actions.add("PAY");
                }
                actions.add("CANCEL");
            }
            case "PAID" -> {
                actions.add("UPDATE_MEETUP");
                if (buyer && order.getBuyerHandoverAt() == null || !buyer && order.getSellerHandoverAt() == null) {
                    actions.add("CONFIRM_HANDOVER");
                }
                actions.add("OPEN_DISPUTE");
                if (!buyer) {
                    actions.add("CANCEL");
                }
            }
            case "DISPUTED" -> {
                DisputeRecord open = disputeMapper.findOpen(order.getId());
                if (open != null && open.getOpenerId() != null && open.getOpenerId() == userId) {
                    actions.add("WITHDRAW_DISPUTE");
                }
            }
            case "COMPLETED" -> {
                if (order.getCompletedAt() != null
                        && !order.getCompletedAt().plusDays(30).isBefore(LocalDateTime.now(Times.SHANGHAI))
                        && reviewMapper.listByOrder(order.getId()).stream().noneMatch(item -> item.getReviewerId() != null && item.getReviewerId() == userId)) {
                    actions.add("REVIEW");
                }
            }
            default -> {
            }
        }
        return actions;
    }

    private Map<String, Object> person(UserEntity user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", fileService.publicUrlIfOwned(user.getAvatarFileId()));
        return data;
    }

    private String coverFileId(long goodsId) {
        List<GoodsImageRow> images = goodsMapper.listImages(List.of(goodsId));
        return images.isEmpty() ? null : images.get(0).getFileId();
    }

    private UserEntity requireVerified(long userId) {
        UserEntity user = userService.requireUser(userId);
        if (!"VERIFIED".equals(user.getVerifyStatus())) {
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
        return user;
    }

    private static boolean isParty(OrderRecord order, long userId) {
        return order.getBuyerId() == userId || order.getSellerId() == userId;
    }

    private static String cleanReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        String text = reason.trim();
        if (text.codePointCount(0, text.length()) > 200) {
            throw ApiException.validation(List.of(new FieldErrorItem("reason", "原因最多 200 字")));
        }
        if (ContactInfo.contains(text)) {
            throw ApiException.of(ErrorCode.CONTACT_INFO_NOT_ALLOWED);
        }
        return text;
    }

    private static List<String> parseStatuses(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String part : status.split(",")) {
            String item = part.trim().toUpperCase();
            if (item.isEmpty()) {
                continue;
            }
            if (!STATUSES.contains(item)) {
                throw ApiException.validation(List.of(new FieldErrorItem("status", "订单状态不合法")));
            }
            list.add(item);
        }
        return list.isEmpty() ? null : list;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
