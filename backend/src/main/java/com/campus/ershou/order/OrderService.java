package com.campus.ershou.order;

import com.campus.ershou.admin.AuditService;
import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.api.Paging;
import com.campus.ershou.common.campus.BuildingMapper;
import com.campus.ershou.common.campus.BuildingView;
import com.campus.ershou.common.campus.CategoryMapper;
import com.campus.ershou.common.campus.CategoryView;
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
import com.campus.ershou.im.ImHub;
import com.campus.ershou.im.NotificationService;
import com.campus.ershou.pay.PaymentMapper;
import com.campus.ershou.pay.PaymentNotify;
import com.campus.ershou.pay.PaymentNotifyMapper;
import com.campus.ershou.pay.PaymentRecord;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.campus.ershou.user.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final Set<String> DISPUTE_REASONS = Set.of(
            "GOODS_MISMATCH", "FALSE_DESCRIPTION", "SUSPECTED_FRAUD", "MISSED_MEETUP");
    private static final Set<String> DISPUTE_RESULTS = Set.of("CANCEL", "COMPLETE", "REJECT");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final OrderTx orderTx;
    private final GoodsMapper goodsMapper;
    private final PaymentMapper paymentMapper;
    private final DisputeMapper disputeMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final BuildingMapper buildingMapper;
    private final CategoryMapper categoryMapper;
    private final FileService fileService;
    private final FileUrls fileUrls;
    private final NotificationService notificationService;
    private final PaymentNotifyMapper paymentNotifyMapper;
    private final ObjectMapper objectMapper;
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
            UserMapper userMapper,
            UserService userService,
            BuildingMapper buildingMapper,
            CategoryMapper categoryMapper,
            FileService fileService,
            FileUrls fileUrls,
            NotificationService notificationService,
            PaymentNotifyMapper paymentNotifyMapper,
            ObjectMapper objectMapper,
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
        this.userMapper = userMapper;
        this.userService = userService;
        this.buildingMapper = buildingMapper;
        this.categoryMapper = categoryMapper;
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.notificationService = notificationService;
        this.paymentNotifyMapper = paymentNotifyMapper;
        this.objectMapper = objectMapper;
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
            order.setGoodsDescription(goods.getDescription());
            order.setGoodsCategoryId(goods.getCategoryId());
            CategoryView category = goods.getCategoryId() == null ? null : categoryMapper.findEnabledById(goods.getCategoryId());
            order.setGoodsCategoryName(category == null ? null : category.getName());
            order.setGoodsCondition(goods.getConditionCode());
            order.setGoodsPrice(goods.getPrice());
            order.setGoodsTradeNote(goods.getTradeNote());
            order.setGoodsMeetupBuildingId(goods.getMeetupBuildingId());
            BuildingView meetup = goods.getMeetupBuildingId() == null ? null : buildingMapper.findEnabledById(goods.getMeetupBuildingId());
            order.setGoodsMeetupBuildingName(meetup == null ? null : meetup.getName());
            order.setGoodsAliasesJson(writeJson(goodsMapper.listAliases(goodsId)));
            order.setGoodsImagesJson(writeJson(goodsMapper.listImages(List.of(goodsId)).stream().map(GoodsImageRow::getFileId).toList()));
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

    public Map<String, Object> cancel(long userId, long orderId, String ip) {
        OrderRecord order = requireParty(userId, orderId, ip);
        order = expireIfNeeded(order);
        if (!"PENDING_PAY".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        boolean buyer = order.getBuyerId() != null && order.getBuyerId() == userId;
        String reason = buyer ? "买家取消" : "卖家取消";
        orderTx.cancelUnpaid(order, buyer ? "BUYER" : "SELLER", userId, reason);
        afterCancel(order, reason);
        auditService.record("BUSINESS", "ORDER_STATUS_CHANGED", null, userId, ip,
                "{\"orderId\":" + orderId + ",\"to\":\"CANCELLED\"}");
        return detail(userId, orderId, ip);
    }

    public OrderRecord syncParty(long userId, long orderId, String ip) {
        return expireIfNeeded(requireParty(userId, orderId, ip));
    }

    public Map<String, Object> handover(long userId, long orderId, String ip) {
        requireVerified(userId);
        OrderRecord order = syncParty(userId, orderId, ip);
        if (!"PAID".equals(order.getStatus()) && !"DISPUTED".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
        boolean buyer = order.getBuyerId() == userId;
        boolean completed = orderTx.confirmHandover(order, buyer);
        if (completed) {
            notifyBoth(order, "ORDER_COMPLETED", "交易已完成", "双方已确认面交。");
            auditService.record("BUSINESS", "ORDER_STATUS_CHANGED", null, userId, ip,
                    "{\"orderId\":" + orderId + ",\"to\":\"COMPLETED\"}");
        }
        pushStatus(orderMapper.findById(orderId));
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> dispute(long userId, long orderId, String reason, String description, List<String> evidence, String ip) {
        requireVerified(userId);
        OrderRecord order = syncParty(userId, orderId, ip);
        if (!"PAID".equals(order.getStatus())) {
            throw ApiException.of(ErrorCode.ORDER_STATUS_INVALID);
        }
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
        List<String> files = evidence == null ? List.of() : evidence.stream().filter(item -> item != null && !item.isBlank()).toList();
        int minEvidence = "GOODS_MISMATCH".equals(cleanReason) || "FALSE_DESCRIPTION".equals(cleanReason) ? 1 : 0;
        if (files.size() < minEvidence || files.size() > 3) {
            throw ApiException.validation(List.of(new FieldErrorItem("evidenceFileIds",
                    minEvidence == 1 ? "至少 1 张、最多 3 张" : "最多 3 张")));
        }
        for (String fileId : files) {
            fileService.requireUsable(fileId, userId, "EVIDENCE");
        }
        orderTx.openDispute(order, userId, cleanReason, text, files);
        notifyBoth(order, "DISPUTE_OPENED", "订单申诉中", "有一方发起了申诉。");
        pushStatus(orderMapper.findById(orderId));
        return detail(userId, orderId, ip);
    }

    public Map<String, Object> withdrawDispute(long userId, long orderId, String ip) {
        OrderRecord order = requireParty(userId, orderId, ip);
        DisputeRecord dispute = disputeMapper.findOpen(orderId);
        if (dispute == null || dispute.getOpenerId() == null || dispute.getOpenerId() != userId) {
            throw ApiException.of(ErrorCode.DISPUTE_NOT_OPEN);
        }
        orderTx.withdrawDispute(order, dispute);
        notifyBoth(order, "DISPUTE_WITHDRAWN", "申诉已撤回", "申诉已撤回，订单回到待面交。");
        pushStatus(orderMapper.findById(orderId));
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
        if (normalized != null && !Set.of("OPEN", "WITHDRAWN", "REJECTED", "RESOLVED").contains(normalized)) {
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
        if (!DISPUTE_RESULTS.contains(outcome)) {
            throw ApiException.validation(List.of(new FieldErrorItem("result", "只能是 CANCEL、COMPLETE 或 REJECT")));
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
        } else if ("REJECT".equals(outcome)) {
            orderTx.resolveReject(order, dispute, text);
        } else {
            boolean fraud = "SUSPECTED_FRAUD".equals(dispute.getReason());
            orderTx.resolveCancel(order, dispute, text, fraud);
            if (fraud) {
                auditService.record("ADMIN", "ADMIN_PUBLISH_BAN", adminId, order.getSellerId(), ip,
                        "{\"orderId\":" + order.getId() + ",\"sellerId\":" + order.getSellerId() + "}");
            }
        }
        notifyBoth(order, "DISPUTE_RESOLVED", "申诉已处理", text);
        auditService.record("ADMIN", "ADMIN_DISPUTE_RESOLVE", adminId, order.getBuyerId(), ip,
                "{\"disputeId\":" + disputeId + ",\"orderId\":" + order.getId() + ",\"result\":\"" + outcome + "\"}");
        pushStatus(orderMapper.findById(order.getId()));
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

    public int countUnreadMessages(long userId) {
        return orderMapper.countUnreadMessages(userId);
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
            orderTx.cancelUnpaid(order, "TIMEOUT", null, "超时释放");
        } catch (ApiException ex) {
            if (ex.getErrorCode() != ErrorCode.ORDER_STATUS_INVALID) {
                throw ex;
            }
        }
        afterCancel(order, "超时释放");
        OrderRecord fresh = orderMapper.findById(order.getId());
        return fresh == null ? order : fresh;
    }

    private void afterCreate(OrderRecord order) {
        notificationService.notify(order.getSellerId(), "ORDER_CREATED", "有新订单", order.getGoodsTitle(), "ORDER", order.getId());
        pushStatus(order);
    }

    private void afterCancel(OrderRecord order, String systemText) {
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
        data.put("goods", goodsSnapshot(order));
        Map<String, Object> handover = new LinkedHashMap<>();
        handover.put("buyerConfirmed", order.getBuyerHandoverAt() != null);
        handover.put("sellerConfirmed", order.getSellerHandoverAt() != null);
        data.put("handover", handover);
        List<PaymentRecord> payments = paymentMapper.listByOrder(order.getId());
        data.put("payment", paymentView(payments, order));
        data.put("paymentEvents", paymentEvents(payments));
        DisputeRecord dispute = disputeMapper.findLatest(order.getId());
        data.put("dispute", dispute == null ? null : disputeView(dispute));
        data.put("allowedActions", viewerId == null ? List.of() : actions(order, viewerId));
        data.put("paidAt", Times.at(order.getPaidAt()));
        data.put("completedAt", Times.at(order.getCompletedAt()));
        data.put("cancelledAt", Times.at(order.getCancelledAt()));
        return data;
    }

    private Map<String, Object> goodsSnapshot(OrderRecord order) {
        Map<String, Object> goods = new LinkedHashMap<>();
        goods.put("id", order.getGoodsId());
        goods.put("title", order.getGoodsTitle());
        goods.put("description", order.getGoodsDescription());
        if (order.getGoodsCategoryId() == null) {
            goods.put("category", null);
        } else {
            Map<String, Object> category = new LinkedHashMap<>();
            category.put("id", order.getGoodsCategoryId());
            category.put("name", order.getGoodsCategoryName());
            goods.put("category", category);
        }
        goods.put("condition", order.getGoodsCondition());
        goods.put("price", order.getGoodsPrice() == null ? order.getAmount() : order.getGoodsPrice());
        goods.put("images", imageViews(readStrings(order.getGoodsImagesJson())));
        if (order.getGoodsMeetupBuildingId() == null) {
            goods.put("meetupBuilding", null);
        } else {
            Map<String, Object> building = new LinkedHashMap<>();
            building.put("id", order.getGoodsMeetupBuildingId());
            building.put("name", order.getGoodsMeetupBuildingName());
            goods.put("meetupBuilding", building);
        }
        goods.put("tradeNote", order.getGoodsTradeNote());
        goods.put("aliases", readStrings(order.getGoodsAliasesJson()));
        return goods;
    }

    private List<Map<String, Object>> imageViews(List<String> fileIds) {
        List<Map<String, Object>> images = new ArrayList<>();
        for (String fileId : fileIds) {
            Map<String, Object> image = new LinkedHashMap<>();
            image.put("fileId", fileId);
            image.put("url", fileUrls.publicUrl(fileId));
            image.put("thumbUrl", fileUrls.thumbUrl(fileId));
            images.add(image);
        }
        return images;
    }

    private List<String> readStrings(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception ex) {
            return List.of();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private Map<String, Object> paymentView(List<PaymentRecord> payments, OrderRecord order) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (payments.isEmpty()) {
            data.put("status", "PENDING_PAY".equals(order.getStatus()) ? "PENDING" : null);
            data.put("reason", order.getCancelReason());
            data.put("amount", order.getAmount());
            return data;
        }
        PaymentRecord payment = payments.get(0);
        data.put("status", payment.getStatus());
        data.put("reason", payment.getReason() == null ? order.getCancelReason() : payment.getReason());
        data.put("amount", payment.getAmount());
        return data;
    }

    private List<Map<String, Object>> paymentEvents(List<PaymentRecord> payments) {
        List<Map<String, Object>> events = new ArrayList<>();
        for (PaymentRecord payment : payments) {
            if (payment.getId() == null) {
                continue;
            }
            for (PaymentNotify notify : paymentNotifyMapper.listByPayment(payment.getId())) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("notifyNo", notify.getNotifyNo());
                item.put("arrivedAt", Times.at(notify.getArrivedAt()));
                item.put("amount", notify.getAmount());
                item.put("adopted", Boolean.TRUE.equals(notify.getAdopted()));
                item.put("reason", notify.getReason());
                events.add(item);
            }
        }
        return events;
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
        List<String> evidenceIds = dispute.getId() == null ? List.of() : disputeMapper.listFiles(dispute.getId());
        data.put("evidenceFileIds", evidenceIds);
        List<Map<String, Object>> evidence = new ArrayList<>();
        for (String fileId : evidenceIds) {
            if (fileId != null && !fileId.isBlank()) {
                evidence.add(fileUrls.privateImage(fileId));
            }
        }
        data.put("evidence", evidence);
        data.put("createdAt", Times.at(dispute.getCreatedAt()));
        data.put("resolvedAt", Times.at(dispute.getResolvedAt()));
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
                if (buyer && order.getBuyerHandoverAt() == null || !buyer && order.getSellerHandoverAt() == null) {
                    actions.add("CONFIRM_HANDOVER");
                }
                actions.add("OPEN_DISPUTE");
            }
            case "DISPUTED" -> {
                DisputeRecord open = disputeMapper.findOpen(order.getId());
                if (open != null && open.getOpenerId() != null && open.getOpenerId() == userId) {
                    actions.add("WITHDRAW_DISPUTE");
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
